package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.engine.InteractionMethod
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class PlantTypeDto(
    val plantTypeName: String,
    val harvestMethod: String,
    val interactionMethod: String,
    val modelBatch: ModelBatchDto
): Dto {

    fun toPlantType(): PlantType<out Entity>{
        return PlantType(
            plantTypeName,
            InteractionMethod.valueOf(harvestMethod),
            InteractionMethod.valueOf(interactionMethod),
            modelBatch.toModelBatch()
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
            val modelBatch = ModelBatchDto.fromPersistentDataContainer(persistentDataContainer)
            if (harvestMethod == null || type  == null || interactionMethod == null || entityClass == null || modelBatch == null) {
                return null
            }

            return PlantTypeDto(type, harvestMethod, interactionMethod, modelBatch)
        }
    }
}
