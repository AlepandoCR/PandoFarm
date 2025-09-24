package com.mapachos.pandoFarm.plants.engine.management

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent

object PlantEventListener: Listener {
    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent) {
        GlobalPlantRegistry.getRegistryForWorld(event.world)?.removePlantsOnChunk(event.chunk)
    }

    @EventHandler
    fun onChunkLoad(event: ChunkUnloadEvent) {
        GlobalPlantRegistry.getRegistryForWorld(event.world)?.removePlantsOnChunk(event.chunk)
    }
}