package com.mapachos.pandoFarm.plants.engine.seeds.listener

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import com.mapachos.pandoFarm.plants.engine.seeds.event.PlaceSeedEvent
import com.mapachos.pandoFarm.util.hasPlant
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class SeedListener(val plugin: PandoFarm): Listener {

    companion object {
        private val placingCooldown: MutableSet<UUID> = Collections.synchronizedSet(mutableSetOf())
    }

    @EventHandler
    fun onSeedPlant(event: PlayerInteractEvent) {
        val player = event.player
        // Only main hand
        if(event.hand != EquipmentSlot.HAND) return
        val item = event.item ?: return
        if(item.amount <= 0) return

        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        if(!Seed.isSeed(item)) return

        // Cooldown guard (1 tick) to avoid double placement
        if (placingCooldown.contains(player.uniqueId)) {
            event.isCancelled = true
            return
        }
        placingCooldown.add(player.uniqueId)
        Bukkit.getScheduler().runTask(plugin, Runnable { placingCooldown.remove(player.uniqueId) })

        val block = event.clickedBlock ?: return
        if(block.type != Material.FARMLAND) return
        val above = block.location.add(0.0,1.0,0.0).block
        if(!above.isEmpty) return

        val seed = Seed.fromItem(item) ?: return
        val interactedLocation = above.location.add(0.5,0.0,0.5)

        if(interactedLocation.hasPlant(plugin)) {
            event.isCancelled = true
            return
        }

        val chunk = interactedLocation.chunk
        if(!chunk.isLoaded) chunk.load()

        // Cancel the event to prevent vanilla handling or other dupes
        event.isCancelled = true

        PlaceSeedEvent(player, seed).callEvent()
        seed.plantSeed(interactedLocation, player)

        if(player.gameMode != GameMode.CREATIVE){
            item.amount -= 1
        }
    }
}