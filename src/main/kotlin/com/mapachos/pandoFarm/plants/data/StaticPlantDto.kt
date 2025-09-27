package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.engine.StaticPlant
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

class StaticPlantDto(
    uniqueIdentifier: String,
    plantType: PlantTypeDto,
    age: Long,
    location: LocationDto,
): PlantDto(
    uniqueIdentifier,
    plantType,
    age,
    location
) {

    fun toStaticPlant(): StaticPlant<out Entity> {
        return StaticPlant.load(this)
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING, uniqueIdentifier)
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

            return StaticPlantDto(uuidString, plantType, age, location)
        }
    }
}
