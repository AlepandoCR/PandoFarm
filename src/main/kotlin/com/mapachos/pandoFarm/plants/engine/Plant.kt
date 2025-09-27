package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.model.Model
import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.model.plant.PlantModelBatchRegistry
import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantGrowEvent
import com.mapachos.pandoFarm.plants.engine.event.plant.SpawnPlantEvent

import com.mapachos.pandoFarm.util.listeners.DynamicListener
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import kotlin.reflect.KClass

abstract class Plant<E: Entity>(
    val location: Location,
    val plantType: PlantType<E>,
    var age: Long = 0, // In seconds
    val uniqueIdentifier: UUID = UUID.randomUUID(),
    val matureAge: Long
) {
    val dynamicListener = DynamicListener()
    val world = location.world!!

    private var modelBatch = PlantModelBatchRegistry.serveID(plantType.modelBatch.id, plantType.modelBatch.entityClass)

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

    fun switchModel(){ // for growth stages
        model.remove()
        model = modelPreset.buildModel(location)
    }

    fun isMature(): Boolean{
        return age >= matureAge
    }

    fun growInterval(): Long {
        return matureAge / 4
    }

    fun getModelBatchID(): String {
        return modelBatch.id
    }

    fun remove(){
        save()
        model.remove()
        dynamicListener.stop()
    }

    fun onSpawn(){
        SpawnPlantEvent(this).callEvent()
    }

    abstract fun toDto(): PlantDto

    /**
     * Called when the plant grows to the next stage
     */
    fun grow(){
        PlantGrowEvent(this).callEvent()
        switchModel()
    }

    open fun harvest(){} // Only HarvestPlants use this method, but it's defined here for it to only be one listener per plant

    abstract fun save()

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