package com.mapachos.pandoFarm.plants.engine.harvest.effect

import org.bukkit.entity.Player

interface HarvestEffect {

    val name: String

    fun effect(player: Player, harvestQuality: Int)

    fun description(): String
}