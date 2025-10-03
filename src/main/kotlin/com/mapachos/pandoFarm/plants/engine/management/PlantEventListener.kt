package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.InteractionMethod
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.Plant.Companion.getPlant
import com.mapachos.pandoFarm.plants.engine.Plant.Companion.isPlant
import com.mapachos.pandoFarm.plants.engine.event.plant.HarvestPlantEvent
import com.mapachos.pandoFarm.plants.engine.event.plant.PlantSpawnEvent
import com.mapachos.pandoFarm.plants.engine.seeds.event.PlaceSeedEvent
import com.mapachos.pandoFarm.util.farmData
import kr.toxicity.model.api.event.ModelInteractAtEvent
import kr.toxicity.model.api.nms.ModelInteractionHand
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
            globalPlantRegistry.removePlantsOnWorld(world)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val world = event.player.world
        if (isPlayerAlone(world, event.player)) {
            globalPlantRegistry.loadPlantsOnWorld(world)
        }
    }

    private fun isPlayerAlone(world: World, player: Player): Boolean =
        world.players.size == 1 && world.players.first() == player

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val fromWorld = event.from
        val toWorld = event.player.world

        if (fromWorld.players.isEmpty() || isPlayerAlone(fromWorld, event.player)) {
            globalPlantRegistry.removePlantsOnWorld(fromWorld)
        }

        // Load plants in the new world if the player is alone, if not, plants should already be loaded
        if(isPlayerAlone(toWorld, event.player)) {
            globalPlantRegistry.loadPlantsOnWorld(toWorld)
        }
    }

    /**
     * When a plant is spawned in the world, add it its respective PlantRegistry
     */
    @EventHandler
    fun onPlantSpawn(event: PlantSpawnEvent<out Entity>){
        val plant = event.plant
        globalPlantRegistry.registerPlant(plant)
    }

    @EventHandler
    fun onPlantEvent(event: PlaceSeedEvent){
        val gardener = event.player
        gardener.farmData().plantedPlants++
    }

    @EventHandler
    fun onPlantHarvest(event: HarvestPlantEvent){
        val harvester = event.player
        harvester.farmData().harvestedPlants++
    }

    @EventHandler
    fun onPlantInteract(event: ModelInteractAtEvent){
        val player = event.player
        val entity = event.hitBox.source()
        if(entity.isPlant()){
            val plant = entity.getPlant(plugin) ?: return
            val interactionMethod = plant.plantType.interactionMethod
            val harvestMethod = plant.plantType.harvestMethod
            val action = event.hand

            when (action) {
                ModelInteractionHand.LEFT -> handleLeftClick(plant, interactionMethod, harvestMethod,player)
                ModelInteractionHand.RIGHT -> handleRightClick(plant, interactionMethod, harvestMethod, player)
            }
        }
    }

    private fun handleLeftClick(
        plant: Plant<out Entity>,
        interactionMethod: InteractionMethod,
        harvestMethod: InteractionMethod,
        player: Player
    ) {
        if(interactionMethod == InteractionMethod.LEFT_CLICK){
            handleInteraction(plant,player)
        }
        if(harvestMethod == InteractionMethod.LEFT_CLICK){
            handleHarvest(plant,player)
        }
    }

    private fun handleRightClick(
        plant: Plant<out Entity>,
        interactionMethod: InteractionMethod,
        harvestMethod: InteractionMethod,
        player: Player
    ) {
        if(interactionMethod == InteractionMethod.RIGHT_CLICK){
            handleInteraction(plant,player)
        }
        if(harvestMethod == InteractionMethod.RIGHT_CLICK){
            handleHarvest(plant, player)
        }
    }

    private fun handleInteraction(plant: Plant<out Entity>, player: Player) {
        plant.interact(player)
    }

    private fun handleHarvest(plant: Plant<out Entity>, player: Player) {
        if(plant.isMature()) plant.harvest(player)
    }
}