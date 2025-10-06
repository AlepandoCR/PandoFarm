package com.mapachos.pandoFarm.player.culling.plant

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks per-player visibility of plants and coordinates global hold/release.
 */
object CullingVisibilityManager {
    private val viewersCount: MutableMap<UUID, Int> = ConcurrentHashMap()
    private val playerVisible: MutableMap<UUID, MutableSet<UUID>> = ConcurrentHashMap()

    private val plugin: PandoFarm get() = PandoFarm.getInstance()

    fun onLook(player: Player, plant: Plant<out Entity>) {
        val pid = player.uniqueId
        val plantId = plant.uniqueIdentifier

        val set = playerVisible.computeIfAbsent(pid) { ConcurrentHashMap.newKeySet() }
        if (set.add(plantId)) {
            val prev = viewersCount.getOrDefault(plantId, 0)
            val next = prev + 1
            viewersCount[plantId] = next
            if (prev == 0) {
                // First viewer: ensure model is active
                plant.release()
            }
        }
        // Always ensure this player sees the model
        plant.show(player)
    }

    fun onStop(player: Player, plant: Plant<out Entity>) {
        val pid = player.uniqueId
        val plantId = plant.uniqueIdentifier

        // Hide for this player
        plant.hide(player)

        // Update sets/counters
        val set = playerVisible[pid]
        if (set != null && set.remove(plantId)) {
            val prev = viewersCount.getOrDefault(plantId, 0)
            val next = (prev - 1).coerceAtLeast(0)
            if (next == 0) {
                viewersCount.remove(plantId)
                // No viewers remain: globally hold the model
                plant.hold()
            } else {
                viewersCount[plantId] = next
            }
        }
    }

    fun onPlayerQuit(player: Player) {
        val pid = player.uniqueId
        val visible = playerVisible.remove(pid) ?: return
        visible.forEach { plantId ->
            val plant = plugin.getGlobalPlantRegistry().getPlant(plantId) ?: return@forEach
            // Hide for this player
            plant.hide(player)
            // Decrement global viewer count
            val prev = viewersCount.getOrDefault(plantId, 0)
            val next = (prev - 1).coerceAtLeast(0)
            if (next == 0) {
                viewersCount.remove(plantId)
                plant.hold()
            } else {
                viewersCount[plantId] = next
            }
        }
    }

    fun onPlantRemoved(plant: Plant<out Entity>) {
        val plantId = plant.uniqueIdentifier
        viewersCount.remove(plantId)
        // Remove plant id from all player sets
        playerVisible.values.forEach { it.remove(plantId) }
    }

    fun isPlayerSeeing(player: Player, plantId: UUID): Boolean {
        val set = playerVisible[player.uniqueId] ?: return false
        return set.contains(plantId)
    }
}
