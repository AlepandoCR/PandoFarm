package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.model.Model
import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.model.plant.PlantModelBatchRegistry
import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto
import com.mapachos.pandoFarm.plants.engine.event.plant.InteractPlantEvent
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantGrowEvent
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantSpawnEvent
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

abstract class Plant<E: Entity>(
    val location: Location,
    val plantType: PlantType<E>,
    var age: Long = 0, // In seconds
    val uniqueIdentifier: UUID = UUID.randomUUID(),
    val matureAge: Long
) {
    val world = location.world!!

    private var modelBatch = PlantModelBatchRegistry.serveID(plantType.modelBatch.id, plantType.modelBatch.entityClass)

    lateinit var model: Model<E>

    lateinit var baseEntity: Entity


    val stage: GrowthStage get() = GrowthStage.fromPlant(this)

    val modelPreset: ModelPreset<E> get() = modelBatch.getModelForStage(stage,location)

    fun spawn(location: Location) {
        onSpawn()
        model = modelPreset.buildModel(location)

        startEntity()
    }

    private fun startEntity() {
        baseEntity = model.entity

        val persistentDataContainer = baseEntity.persistentDataContainer

        toDto().applyOnPersistentDataContainer(persistentDataContainer)
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

    fun remove(plugin: PandoFarm) {
        save(plugin)
        model.remove()
    }

    fun onSpawn(){
        PlantSpawnEvent(this).callEvent()
    }

    abstract fun toDto(): PlantDto

    /**
     * Called when the plant grows to the next stage
     */
    fun grow(){
        PlantGrowEvent(this).callEvent()
        switchModel()
    }

    open fun harvest(player: Player){} // Only HarvestPlants use this method, but it's defined here for it to only be one listener per plant

    abstract fun save(plugin: PandoFarm)

    fun interact(player: Player) {
        InteractPlantEvent(player, this).callEvent()
    }

    companion object {

        fun Entity.getPlant(plugin: PandoFarm): Plant<out Entity>?{
            val pdc = this.persistentDataContainer

            val globalPlantRegistry = plugin.getGlobalPlantRegistry()
            return when {
                this.isHarvestPlant() -> {
                    val dto = HarvestPlantDto.fromPersistentDataContainer(pdc) ?: return null
                    val uuid = UUID.fromString(dto.uniqueIdentifier)
                    globalPlantRegistry.getPlant(uuid)
                }
                this.isStaticPlant() -> {
                    val dto = StaticPlantDto.fromPersistentDataContainer(pdc) ?: return null
                    val uuid = UUID.fromString(dto.uniqueIdentifier)
                    globalPlantRegistry.getPlant(uuid)
                }
                else -> null
            }
        }

        fun Entity.isPlant(): Boolean {
            val pdc = this.persistentDataContainer
            return this.isHarvestPlant() || this.isStaticPlant()
        }

        fun Entity.isHarvestPlant(): Boolean {
            val pdc = this.persistentDataContainer
            return HarvestPlantDto.fromPersistentDataContainer(pdc) != null
        }

        fun Entity.isStaticPlant(): Boolean {
            val pdc = this.persistentDataContainer
            return StaticPlantDto.fromPersistentDataContainer(pdc) != null
        }
    }

}