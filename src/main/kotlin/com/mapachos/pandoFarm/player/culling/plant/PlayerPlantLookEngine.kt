package com.mapachos.pandoFarm.player.culling.plant

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.player.culling.LookDirection.Companion.canLook
import com.mapachos.pandoFarm.player.event.PlayerLookAtPlantEvent
import com.mapachos.pandoFarm.player.event.PlayerStopLookingPlantEvent
import com.mapachos.pandoFarm.util.timer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * Lightweight per-player engine that tracks when plants become visible/invisible to the player.
 * Uses player.canSee(entity) and fires events only on state changes.
 */
class PlayerPlantLookEngine(
    private val plugin: PandoFarm,
    private val player: Player,
    private val scanRadius: Double = 48.0,
    private val periodTicks: Long = 5L
) {
    private var task: BukkitTask? = null
    private val visiblePlants: MutableSet<UUID> = mutableSetOf()

    fun start() {
        if (task != null) return
        task = timer( { tick() },0, periodTicks)
    }

    fun stop() {
        task?.cancel()
        task = null
        visiblePlants.clear()
    }

    private fun tick() {
        if (!player.isOnline || player.isDead) {
            stop()
            return
        }
        val world = player.world
        val registry = plugin.getGlobalPlantRegistry().getRegistryForWorld(world)
        if (registry.registry.isEmpty()) return

        val eye = player.eyeLocation
        val nowVisible = HashSet<UUID>()

        registry.registry.forEach { plant ->
            val location = plant.location
            if (location.world != world) return@forEach
            val distSq = location.distanceSquared(eye)
            if (distSq > scanRadius * scanRadius) return@forEach

            if (player.hasLineOfSight(plant.location)) {
                if(player.canLook(plant.baseEntity)) nowVisible.add(plant.uniqueIdentifier)
            }
        }

        val entered = nowVisible - visiblePlants
        val exited = visiblePlants - nowVisible

        if (entered.isNotEmpty() || exited.isNotEmpty()) {
            val global = plugin.getGlobalPlantRegistry()
            entered.forEach { id ->
                val p = global.getPlant(id) ?: return@forEach
                PlayerLookAtPlantEvent(player, p).callEvent()
            }
            exited.forEach { id ->
                val p = global.getPlant(id) ?: return@forEach
               PlayerStopLookingPlantEvent(player, p).callEvent()
            }
        }

        visiblePlants.clear()
        visiblePlants.addAll(nowVisible)
    }
}
