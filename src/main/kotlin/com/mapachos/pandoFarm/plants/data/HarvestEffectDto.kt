package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestEffectDto(
    val description: String
): Dto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.DESCRIPTION.toNamespacedKey(), PersistentDataType.STRING, description)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestEffectDto? {
            val description = persistentDataContainer.get(DataNamespacedKey.DESCRIPTION.toNamespacedKey(), PersistentDataType.STRING)
            return description?.let { HarvestEffectDto(it) }
        }
    }
}