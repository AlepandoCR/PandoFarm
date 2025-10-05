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
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import kr.toxicity.model.api.event.ModelInteractAtEvent
import kr.toxicity.model.api.nms.ModelInteractionHand
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

class PlantEventListener(val plugin: PandoFarm): Listener {

    val globalPlantRegistry = plugin.getGlobalPlantRegistry()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val world = event.player.world
        plugin.logger.info("[PlantEventListener] PlayerQuit: ${event.player.name} from world=${world.name}, playersLeft=${world.players.size - 1}")
        if (world.players.isEmpty() || isPlayerAlone(world, event.player)) {
            plugin.logger.info("[PlantEventListener] Removing plants on world=${world.name} due to last player leaving")
            globalPlantRegistry.removePlantsOnWorld(world)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val world = event.player.world
        plugin.logger.info("[PlantEventListener] PlayerJoin: ${event.player.name} to world=${world.name}, playersNow=${world.players.size}")
        if (isPlayerAlone(world, event.player)) {
            plugin.logger.info("[PlantEventListener] Loading plants on world=${world.name} (first player present)")
            globalPlantRegistry.loadPlantsOnWorld(world)
        }
    }

    private fun isPlayerAlone(world: World, player: Player): Boolean =
        world.players.size == 1 && world.players.first() == player

    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val fromWorld = event.from
        val toWorld = event.player.world
        plugin.logger.info("[PlantEventListener] PlayerChangedWorld: ${event.player.name} from=${fromWorld.name} to=${toWorld.name}")

        if (fromWorld.players.isEmpty() || isPlayerAlone(fromWorld, event.player)) {
            plugin.logger.info("[PlantEventListener] Removing plants on world=${fromWorld.name} (last player left)")
            globalPlantRegistry.removePlantsOnWorld(fromWorld)
        }

        // Load plants in the new world if the player is alone, if not, plants should already be loaded
        if(isPlayerAlone(toWorld, event.player)) {
            plugin.logger.info("[PlantEventListener] Loading plants on world=${toWorld.name} (first player present)")
            globalPlantRegistry.loadPlantsOnWorld(toWorld)
        }
    }

//    @EventHandler
//    fun onChunkUnload(event: ChunkUnloadEvent) {
//        val chunk = event.chunk
//        plugin.logger.info("[PlantEventListener] ChunkUnload: world=${chunk.world.name} chunk=(${chunk.x},${chunk.z})")
//        globalPlantRegistry.removePlantsOnChunk(chunk)
//    }
//
//    @EventHandler
//    fun onChunkLoad(event: ChunkLoadEvent) {
//        val chunk = event.chunk
//        plugin.logger.info("[PlantEventListener] ChunkLoad: world=${chunk.world.name} chunk=(${chunk.x},${chunk.z})")
//        globalPlantRegistry.loadPlantsOnChunk(chunk)
//    }

    /**
     * When a plant is spawned in the world, add it its respective PlantRegistry
     */
    @EventHandler
    fun onPlantSpawn(event: PlantSpawnEvent<out Entity>){
        val plant = event.plant
        plugin.logger.info("[PlantEventListener] PlantSpawnEvent: id=${plant.uniqueIdentifier} type=${plant.plantType.name} world=${plant.world.name} at=(${plant.location.blockX},${plant.location.blockY},${plant.location.blockZ})")
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
        val plant = event.plant
        val harvest = plant.harvest
        plugin.getGlobalPlantRegistry().removePlant(plant)
        harvester.farmData().harvestedPlants++
        harvester.inventory.addItem(harvest.harvestItem.buildItem())

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

    @EventHandler
    fun onPlantRightClick(event: PlayerInteractAtEntityEvent){
        val player = event.player
        val entity = event.rightClicked
        if(entity.isPlant()){
            val plant = entity.getPlant(plugin) ?: return
            val interactionMethod = plant.plantType.interactionMethod
            val harvestMethod = plant.plantType.harvestMethod

            handleRightClick(plant, interactionMethod, harvestMethod, player)
        }
    }

    @EventHandler
    fun onPlantLeftClick(event: PrePlayerAttackEntityEvent){
        val player = event.player
        val entity = event.attacked
        if(entity.isPlant()){
            val plant = entity.getPlant(plugin) ?: return
            val interactionMethod = plant.plantType.interactionMethod
            val harvestMethod = plant.plantType.harvestMethod

            handleLeftClick(plant, interactionMethod, harvestMethod, player)
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