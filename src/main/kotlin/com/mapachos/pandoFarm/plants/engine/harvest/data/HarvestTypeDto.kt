package com.mapachos.pandoFarm.plants.engine.harvest.data

import com.mapachos.pandoFarm.database.data.ContainerDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestType
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class HarvestTypeDto(
    val harvestTypeName: String,
    val effect: HarvestEffectDto,
    val customModelComponentString: String,
    val material: String
): ContainerDto {

    fun toHarvestType(): HarvestType {
        return HarvestType(
            harvestTypeName,
            customModelComponentString,
            effect.toHarvestEffectType(),
            Material.valueOf(material)
        )
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.HARVEST_TYPE_NAME.toNamespacedKey(), PersistentDataType.STRING, harvestTypeName)
        persistentDataContainer.set(DataNamespacedKey.HARVEST_TYPE_CUSTOM_MODEL_COMPONENT_STRING.toNamespacedKey(), PersistentDataType.STRING, customModelComponentString)
        persistentDataContainer.set(DataNamespacedKey.MATERIAL.toNamespacedKey(), PersistentDataType.STRING, material)
        effect.applyOnPersistentDataContainer(persistentDataContainer)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): HarvestTypeDto? {
            val type = persistentDataContainer.get(DataNamespacedKey.HARVEST_TYPE_NAME.toNamespacedKey(), PersistentDataType.STRING)
            val customModelComponentString = persistentDataContainer.get(DataNamespacedKey.HARVEST_TYPE_CUSTOM_MODEL_COMPONENT_STRING.toNamespacedKey(), PersistentDataType.STRING)
            val material = persistentDataContainer.get(DataNamespacedKey.MATERIAL.toNamespacedKey(), PersistentDataType.STRING)
            val effect = HarvestEffectDto.fromPersistentDataContainer(persistentDataContainer)

            if (type == null || effect == null || customModelComponentString == null || material == null) return null
            return HarvestTypeDto(type, effect, customModelComponentString, material)
        }
    }
}