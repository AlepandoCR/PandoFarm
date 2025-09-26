package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.PlantType
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class StaticPlantDto(
    uniqueIdentifier: UUID,
    plantType: PlantTypeDto,
    age: Long,
    location: LocationDto,
): PlantDto(
    uniqueIdentifier,
    plantType,
    age,
    location
) {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING, uniqueIdentifier.toString())
        persistentDataContainer.set(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG, age)
        location.applyOnPersistentDataContainer(persistentDataContainer)
        plantType.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): StaticPlantDto? {
            val uuidString = persistentDataContainer.get(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING)
            val age = persistentDataContainer.get(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG)
            val location = LocationDto.fromPersistentDataContainer(persistentDataContainer)
            val plantType = PlantTypeDto.fromPersistentDataContainer(persistentDataContainer)

            if (uuidString.isNullOrBlank() || plantType == null || age == null || location == null) return null
            val uuid = try { UUID.fromString(uuidString) } catch (_: IllegalArgumentException) { return null }

            return StaticPlantDto(uuid, plantType, age, location)
        }
    }
}
