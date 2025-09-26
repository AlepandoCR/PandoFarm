package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.model.plant.PlantModelBatchRegistry
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer

data class ModelBatchDto(
    val modelBatchId: String,
    val entityClass: String,
): Dto {
    fun toModelBatch(): PlantModelBatch<out Entity> {
        val clazz: Class<out Entity> = try {
            Class.forName("org.bukkit.entity.$entityClass").asSubclass(Entity::class.java)
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("$entityClass Not found", e)
        }
        PlantModelBatchRegistry.getTypedById(modelBatchId, clazz)?.let { return it }
        return PlantModelBatchRegistry.serveID(modelBatchId, clazz)
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.MODEL_BATCH.toNamespacedKey(), org.bukkit.persistence.PersistentDataType.STRING, modelBatchId)
        persistentDataContainer.set(DataNamespacedKey.ENTITY_CLASS.toNamespacedKey(), org.bukkit.persistence.PersistentDataType.STRING, entityClass)
    }

    companion object{
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): ModelBatchDto? {
            val modelBatchId = persistentDataContainer.get(DataNamespacedKey.MODEL_BATCH.toNamespacedKey(), org.bukkit.persistence.PersistentDataType.STRING) ?: return null
            val entityClass = persistentDataContainer.get(DataNamespacedKey.ENTITY_CLASS.toNamespacedKey(), org.bukkit.persistence.PersistentDataType.STRING) ?: return null
            return ModelBatchDto(modelBatchId, entityClass)
        }
    }
}