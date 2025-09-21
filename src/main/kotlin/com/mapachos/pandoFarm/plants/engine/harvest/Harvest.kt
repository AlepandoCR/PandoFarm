package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import org.bukkit.Material
import org.bukkit.entity.Player

abstract class Harvest<H: HarvestType, E: HarvestEffectType, M: Material>(
    private var pQuality: Int,
    val harvestType: H,
    val harvestEffectType: E,
    val material: M
) {
    private val maxQuality = 20

    init {
        if (pQuality > maxQuality) {pQuality = maxQuality}
    }

    fun getQuality(): Int {
        return pQuality
    }

    fun effect(player: Player) {
        harvestEffectType.harvestEffect.effect(player, pQuality)
    }
}