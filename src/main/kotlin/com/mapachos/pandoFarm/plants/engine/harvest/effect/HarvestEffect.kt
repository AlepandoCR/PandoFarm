package com.mapachos.pandoFarm.plants.engine.harvest.effect

import org.bukkit.entity.Player

interface HarvestEffect {

    fun effect(player: Player, harvestQuality: Int)

    fun description(): String
}