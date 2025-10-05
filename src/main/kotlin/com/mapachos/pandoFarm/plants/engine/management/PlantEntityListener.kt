package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.Plant.Companion.getPlant
import com.mapachos.pandoFarm.plants.engine.Plant.Companion.isPlant
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class PlantEntityListener(private val plugin: PandoFarm): Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent){
        val entity = event.entity
        if(!entity.isPlant()) return
        val plant = entity.getPlant(plugin) ?: return
        event.drops.clear()
        plugin.getGlobalPlantRegistry().removePlant(plant)
    }
}
