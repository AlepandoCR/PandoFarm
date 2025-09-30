package com.mapachos.pandoFarm.plants.engine.seeds.listener

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import com.mapachos.pandoFarm.plants.engine.seeds.event.PlaceSeedEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class SeedListener(val plugin: PandoFarm): Listener {
    @EventHandler
    fun onSeedPlant(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return

        if(Seed.isSeed(item)){
            val seed = Seed.fromItem(item) ?: return
            val interactedLocation = event.clickedBlock?.location ?: return
            PlaceSeedEvent(player, seed).callEvent()
            seed.plantSeed(interactedLocation, player)
        }
    }
}