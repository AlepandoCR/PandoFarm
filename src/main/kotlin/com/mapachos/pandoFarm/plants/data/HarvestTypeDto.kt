package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestTypeDto(
    val harvestType: String,
    val effect: HarvestEffectDto
): Dto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.HARVEST_TYPE.toNamespacedKey(), PersistentDataType.STRING, harvestType)
        effect.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestTypeDto? {
            val type = persistentDataContainer.get(DataNamespacedKey.HARVEST_TYPE.toNamespacedKey(), PersistentDataType.STRING)
            val effect = HarvestEffectDto.fromPersistentDataContainer(persistentDataContainer)
            if (type == null || effect == null) return null
            return HarvestTypeDto(type, effect)
        }
    }
}