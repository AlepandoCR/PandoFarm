package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.database.data.LocationDto.Companion.toDto
import com.mapachos.pandoFarm.model.Model
import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.PlantDto

import com.mapachos.pandoFarm.util.DynamicListener
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import kotlin.reflect.KClass

abstract class Plant<E: Entity>(
    val location: Location,
    val plantType: PlantType,
    private var modelBatch: PlantModelBatch<E>,
    var age: Long = 0, // In seconds
    val uniqueIdentifier: UUID = UUID.randomUUID(),
) {
    val dynamicListener = DynamicListener()
    val chunk = location.chunk

    lateinit var model: Model<E>

    init {
        dynamicListener.setListener(listener(plantType.harvestMethod.eventClass, plantType.interactionMethod.eventClass))
    }

    val stage: GrowthStage get() = GrowthStage.fromPlant(this)

    val modelPreset: ModelPreset<E> get() = modelBatch.getModelForStage(stage,location)

    fun spawn(location: Location) {
        onSpawn()
        model = modelPreset.buildModel(location)
    }

    fun switchModelBatch(newBatch: PlantModelBatch<E>){ // for different varieties
        model.remove()
        modelBatch = newBatch
        model = modelPreset.buildModel(location)
    }

    fun switchModel(newPreset: ModelPreset<E>){ // for growth stages
        model.remove()
        model = modelPreset.buildModel(location)
    }

    fun isMature(): Boolean{
        return age >= matureAge()
    }

    fun growInterval(): Long {
        return matureAge() / 4
    }

    fun getModelBatchID(): String {
        return modelBatch.id
    }

    abstract fun toDto(): PlantDto

    abstract fun onSpawn()

    /**
     * Called when the plant grows to the next stage
     */
    abstract fun grow()

    abstract fun harvest()

    // In seconds
    abstract fun matureAge(): Long

    /**
     * Called every second
     */
    abstract fun task()

    abstract fun destroy()

    abstract fun save()

    abstract fun load(dto: PlantDto)

    abstract fun interact()

    private fun <C: KClass<out Event>, Z: KClass<out Event>>listener(harvestEventClass: C, interactEventClass: Z): Listener {
        val listener =  object : Listener {
            @EventHandler
            fun onInteract(event: Event){
                if(harvestEventClass.isInstance(event)){
                    harvest()
                }

                if(interactEventClass.isInstance(event)){
                    interact()
                }
            }
        }
        return listener
    }
}