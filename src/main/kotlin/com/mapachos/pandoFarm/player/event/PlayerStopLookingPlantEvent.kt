package com.mapachos.pandoFarm.player.event

import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.event.FarmEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * Fired once when a plant entity leaves the player's field of view.
 * Lightweight and only triggered on state change.
 */
class PlayerStopLookingPlantEvent(
    val player: Player,
    val plant: Plant<out Entity>
) : FarmEvent()

