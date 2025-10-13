package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.engine.harvest.data.HarvestTypeDto
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class HarvestType(
    val name: String,
    val customModelComponentString: String,
    val harvestEffectType: HarvestEffectType,
    val material: Material
) {

    fun buildHarvest(quality: Int = 20): Harvest {
        return Harvest(quality, this)
    }

    fun toDto(): HarvestTypeDto{
        return HarvestTypeDto(this.name, harvestEffectType.toDto(),customModelComponentString, material.name)
    }
}