package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.data.HarvestDto
import org.bukkit.entity.Player

abstract class Harvest<H: HarvestType>(
    private var pQuality: Int,
    val harvestType: H
) {
    val material = harvestType.material
    val harvestItem = HarvestItem(this)

    private val maxQuality = 20

    init {
        if (pQuality !in 0..maxQuality) {
            pQuality = maxQuality
        }
    }

    fun getQuality(): Int {
        return pQuality
    }

    fun effect(player: Player) {
        harvestType.harvestEffectType.harvestEffect.effect(player, pQuality)
    }

    fun toDto(): HarvestDto {
        return HarvestDto(
            material.name,
            pQuality,
            harvestType.toDto()
        )
    }

    fun load(){

    }

    fun save(){

    }
}