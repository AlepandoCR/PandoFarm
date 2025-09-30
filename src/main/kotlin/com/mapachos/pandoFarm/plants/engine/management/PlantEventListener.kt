package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantSpawnEvent
import com.mapachos.pandoFarm.plants.engine.seeds.event.PlaceSeedEvent
import com.mapachos.pandoFarm.util.farmData
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlantEventListener(val plugin: PandoFarm): Listener {

    val globalPlantRegistry = plugin.getGlobalPlantRegistry()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val world = event.player.world
        if (world.players.isEmpty() || isPlayerAlone(world, event.player)) {
            globalPlantRegistry.removeWorld(world)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val world = event.player.world
        if (isPlayerAlone(world, event.player)) {
            globalPlantRegistry.getRegistryForWorld(world).loadPlantsOnWorld(world)
        }
    }

    private fun isPlayerAlone(world: World, player: Player): Boolean =
        (world.players.size == 1 && world.players.first() == player)

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val fromWorld = event.from
        val toWorld = event.player.world

        if (fromWorld.players.isEmpty() || isPlayerAlone(fromWorld, event.player)) {
            globalPlantRegistry.removeWorld(fromWorld)
        }

        // Load plants in the new world if the player is alone, if not, plants should already be loaded
        if(isPlayerAlone(toWorld, event.player)) {
            globalPlantRegistry.getRegistryForWorld(toWorld).loadPlantsOnWorld(toWorld)
        }
    }

    /**
     * When a plant is spawned in the world, add it its respective PlantRegistry
     */
    @EventHandler
    fun onPlantSpawn(event: PlantSpawnEvent<out Entity>){
        val plant = event.plant
        globalPlantRegistry.getRegistryForWorld(plant.world).addPlant(plant)
    }

    @EventHandler
    fun onPlantEvent(event: PlaceSeedEvent){
        val gardener = event.player
        gardener.farmData().plantedPlants++
    }
}