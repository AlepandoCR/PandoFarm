package com.mapachos.pandoFarm.plants.engine.harvest.effect.types

import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffect
import org.bukkit.entity.Player

object NoneHarvestEffect: HarvestEffect {
    override val name: String
        get() = "None"

    override fun effect(player: Player, harvestQuality: Int) {
        // No effect
    }

    override fun description(): String {
        return "No effect"
    }
}