package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.harvest.data.HarvestDto
import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class HarvestPlantDto(
    uniqueIdentifier: String,
    plantType: PlantTypeDto,
    age: Long,
    location: LocationDto,
    val harvestDto: HarvestDto,
): PlantDto(uniqueIdentifier, plantType,
    age,
    location
) {
    fun toHarvestPlant(): HarvestPlant<out Entity> {
        return HarvestPlant.load(this)
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING, uniqueIdentifier)
        persistentDataContainer.set(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG, age)
        location.applyOnPersistentDataContainer(persistentDataContainer)
        harvestDto.applyOnPersistentDataContainer(persistentDataContainer)
        plantType.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestPlantDto? {
            val uuidString = persistentDataContainer.get(DataNamespacedKey.UUID.toNamespacedKey(), PersistentDataType.STRING)
            val age = persistentDataContainer.get(DataNamespacedKey.AGE.toNamespacedKey(), PersistentDataType.LONG)
            val location = LocationDto.fromPersistentDataContainer(persistentDataContainer)
            val harvestDto = HarvestDto.fromPersistentDataContainer(persistentDataContainer)
            val plantType = PlantTypeDto.fromPersistentDataContainer(persistentDataContainer)

            if (uuidString.isNullOrBlank() || plantType == null || age == null || location == null || harvestDto == null ) return null

            return HarvestPlantDto(uuidString, plantType, age, location, harvestDto)
        }
    }
}
