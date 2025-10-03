package com.mapachos.pandoFarm.plants.engine.seeds.listener

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import com.mapachos.pandoFarm.plants.engine.seeds.event.PlaceSeedEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class SeedListener(val plugin: PandoFarm): Listener {
    @EventHandler
    fun onSeedPlant(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return // Avoid double event call (main hand)
        val player = event.player
        val item = event.item ?: return
        if(item.amount <= 0) return

        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        if(!Seed.isSeed(item)) return

        val block = event.clickedBlock ?: return
        if(block.type != Material.FARMLAND) return
        val above = block.location.add(0.0,1.0,0.0).block
        if(!above.isEmpty) return

        val seed = Seed.fromItem(item) ?: return
        val interactedLocation = above.location.add(0.5,0.0,0.5)

        val chunk = interactedLocation.chunk
        if(!chunk.isLoaded) chunk.load()

        PlaceSeedEvent(player, seed).callEvent()

        seed.plantSeed(interactedLocation, player)

        if(player.gameMode != GameMode.CREATIVE){
            item.amount -= 1
        }

        event.isCancelled = true
    }
}