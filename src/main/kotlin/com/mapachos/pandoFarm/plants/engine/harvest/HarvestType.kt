package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.data.HarvestTypeDto
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import org.bukkit.Material

class HarvestType(
    val name: String,
    val customModelComponentString: String,
    val harvestEffectType: HarvestEffectType,
    val material: Material
) {

    fun toDto(): HarvestTypeDto{
        return HarvestTypeDto(this.name, harvestEffectType.toDto(),customModelComponentString, material.name)
    }
}