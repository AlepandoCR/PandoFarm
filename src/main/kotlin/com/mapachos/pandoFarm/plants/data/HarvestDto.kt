package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestDto(
    val material: String,
    val quality: Int,
    val harvestType: HarvestTypeDto
): Dto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.MATERIAL.toNamespacedKey(), PersistentDataType.STRING, material)
        persistentDataContainer.set(DataNamespacedKey.QUALITY.toNamespacedKey(), PersistentDataType.INTEGER, quality)
        harvestType.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestDto? {
            val material = persistentDataContainer.get(DataNamespacedKey.MATERIAL.toNamespacedKey(), PersistentDataType.STRING) ?: return null
            val quality = persistentDataContainer.get(DataNamespacedKey.QUALITY.toNamespacedKey(), PersistentDataType.INTEGER) ?: return null
            val harvestType = HarvestTypeDto.fromPersistentDataContainer(persistentDataContainer) ?: return null
            return HarvestDto(material, quality, harvestType)
        }
    }
}
