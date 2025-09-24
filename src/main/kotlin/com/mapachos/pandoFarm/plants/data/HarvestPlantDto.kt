package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class HarvestPlantDto(
    uniqueIdentifier: UUID,
    plantType: String,
    age: Long,
    location: LocationDto,
    modelBatch: String,
    val harvestDto: HarvestDto,
): PlantDto(uniqueIdentifier, plantType,
    age,
    location, modelBatch
) {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING, uniqueIdentifier.toString())
        persistentDataContainer.set(DataNamespacedKey.PLANT_TYPE.toNamespacedKey(),PersistentDataType.STRING ,plantType)
        persistentDataContainer.set(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG, age)
        persistentDataContainer.set(DataNamespacedKey.MODEL_BATCH.toNamespacedKey(), PersistentDataType.STRING ,modelBatch)
        location.applyOnPersistentDataContainer(persistentDataContainer)
        harvestDto.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestPlantDto? {
            val uuidString = persistentDataContainer.get(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING)
            val plantType = persistentDataContainer.get(DataNamespacedKey.PLANT_TYPE.toNamespacedKey(), PersistentDataType.STRING)
            val age = persistentDataContainer.get(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG)
            val location = LocationDto.fromPersistentDataContainer(persistentDataContainer)
            val modelBatch = persistentDataContainer.get(DataNamespacedKey.MODEL_BATCH.toNamespacedKey(), PersistentDataType.STRING)
            val harvestDto = HarvestDto.fromPersistentDataContainer(persistentDataContainer)

            if (uuidString.isNullOrBlank() || plantType == null || age == null || location == null || modelBatch == null || harvestDto == null) return null
            val uuid = try { UUID.fromString(uuidString) } catch (_: IllegalArgumentException) { return null }

            return HarvestPlantDto(uuid, plantType, age, location, modelBatch, harvestDto)
        }
    }
}
