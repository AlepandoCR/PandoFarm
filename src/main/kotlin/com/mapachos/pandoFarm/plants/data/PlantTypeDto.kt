package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.ContainerDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.InteractionMethod
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.StaticPlant
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantPlantEvent
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class PlantTypeDto(
    val plantTypeName: String,
    val harvestMethod: String,
    val interactionMethod: String,
    val modelBatch: ModelBatchDto,
    val matureAge: Long,
    val harvestName: String? = null
): ContainerDto {

    fun createPlant(location: Location, gardener: Entity): Plant<out Entity> {
        val plant = if(harvestName.isNullOrBlank()) {
            buildStaticPlant(location)
        }else{
            buildHarvestPlant(location)
        }

        PlantPlantEvent(plant, gardener).callEvent()

        return plant
    }

    private fun buildStaticPlant(location: Location): StaticPlant<out Entity> {
        return StaticPlant(location,toPlantType(), matureAge = matureAge)
    }

    private fun buildHarvestPlant(location: Location): Plant<out Entity> {
        harvestName ?: throw IllegalArgumentException("Harvest name is null for plant type $plantTypeName")
        val harvestType = HarvestTypeRegistry.getHarvestByName(harvestName) ?: throw IllegalArgumentException("Harvest type $harvestName not found")
        return HarvestPlant(location, toPlantType(),harvestType.buildHarvest() , matureAge = matureAge)
    }


    fun toPlantType(): PlantType<out Entity>{
        return PlantType(
            plantTypeName,
            InteractionMethod.valueOf(harvestMethod),
            InteractionMethod.valueOf(interactionMethod),
            modelBatch.toModelBatch(),
            matureAge
        )
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.PLANT_TYPE.toNamespacedKey(), PersistentDataType.STRING, plantTypeName)
        persistentDataContainer.set(DataNamespacedKey.HARVEST_METHOD.toNamespacedKey(), PersistentDataType.STRING, harvestMethod)
        persistentDataContainer.set(DataNamespacedKey.INTERACTION_METHOD.toNamespacedKey(), PersistentDataType.STRING, interactionMethod)
        modelBatch.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): PlantTypeDto? {
            val type = persistentDataContainer.get(DataNamespacedKey.PLANT_TYPE.toNamespacedKey(), PersistentDataType.STRING)
            val harvestMethod = persistentDataContainer.get(DataNamespacedKey.HARVEST_METHOD.toNamespacedKey(), PersistentDataType.STRING)
            val interactionMethod = persistentDataContainer.get(DataNamespacedKey.INTERACTION_METHOD.toNamespacedKey(), PersistentDataType.STRING)
            val entityClass = persistentDataContainer.get(DataNamespacedKey.ENTITY_CLASS.toNamespacedKey(), PersistentDataType.STRING)
            val matureAge = persistentDataContainer.get(DataNamespacedKey.MATURE_AGE.toNamespacedKey(), PersistentDataType.LONG)
            val modelBatch = ModelBatchDto.fromPersistentDataContainer(persistentDataContainer)
            if (harvestMethod == null || type  == null || interactionMethod == null || entityClass == null || modelBatch == null || matureAge == null) {
                return null
            }

            return PlantTypeDto(type, harvestMethod, interactionMethod, modelBatch, matureAge)
        }
    }
}
