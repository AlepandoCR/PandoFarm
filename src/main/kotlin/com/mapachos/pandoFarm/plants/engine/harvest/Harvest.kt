package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.engine.harvest.data.HarvestDto
import org.bukkit.entity.Player

class Harvest(
    private var pQuality: Int,
    val harvestType: HarvestType
) {
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
            pQuality,
            harvestType.toDto()
        )
    }

    fun load(){

    }

    fun save(){

    }
}