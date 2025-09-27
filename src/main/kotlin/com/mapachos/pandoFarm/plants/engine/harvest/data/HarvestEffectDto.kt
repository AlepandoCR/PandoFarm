package com.mapachos.pandoFarm.plants.engine.harvest.data

import com.mapachos.pandoFarm.database.data.ContainerDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestEffectDto(
    val harvestEffectName: String,
    val description: String
): ContainerDto {
    fun toHarvestEffectType(): HarvestEffectType {
        return HarvestEffectType(HarvestEffectRegistry.getByName(harvestEffectName))
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.HARVEST_EFFECT_NAME.toNamespacedKey(), PersistentDataType.STRING, harvestEffectName)
        persistentDataContainer.set(DataNamespacedKey.HARVEST_EFFECT_DESCRIPTION.toNamespacedKey(), PersistentDataType.STRING, description)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestEffectDto? {
            val name = persistentDataContainer.get(DataNamespacedKey.HARVEST_EFFECT_NAME.toNamespacedKey(), PersistentDataType.STRING) ?: return null
            val description = persistentDataContainer.get(DataNamespacedKey.HARVEST_EFFECT_DESCRIPTION.toNamespacedKey(), PersistentDataType.STRING)
            return description?.let { HarvestEffectDto(name,it) }
        }
    }
}