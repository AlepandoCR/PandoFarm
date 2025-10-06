package com.mapachos.pandoFarm.player.culling.plant.listener

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.player.culling.plant.CullingVisibilityManager
import com.mapachos.pandoFarm.player.event.PlayerLookAtPlantEvent
import com.mapachos.pandoFarm.player.event.PlayerStopLookingPlantEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Plant Culling: per-player show/hide, global hold/release when viewers change.
 */
class PlantCullingListener(val plugin: PandoFarm): Listener {

    @EventHandler
    fun onPlayerStartLookingAtPlant(event: PlayerLookAtPlantEvent){
        CullingVisibilityManager.onLook(event.player, event.plant)
    }

    @EventHandler
    fun onPlayerStopLookingAtPlant(event: PlayerStopLookingPlantEvent){
        CullingVisibilityManager.onStop(event.player, event.plant)
    }
}