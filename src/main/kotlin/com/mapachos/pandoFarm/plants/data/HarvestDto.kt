package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestDto(
    val quality: Int,
    val harvestType: HarvestTypeDto
): Dto {
    fun toHarvest(): Harvest {
        return Harvest(quality, harvestType.toHarvestType())
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.QUALITY.toNamespacedKey(), PersistentDataType.INTEGER, quality)
        harvestType.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestDto? {
            val quality = persistentDataContainer.get(DataNamespacedKey.QUALITY.toNamespacedKey(), PersistentDataType.INTEGER) ?: return null
            val harvestType = HarvestTypeDto.fromPersistentDataContainer(persistentDataContainer) ?: return null
            return HarvestDto(quality, harvestType)
        }
    }
}
