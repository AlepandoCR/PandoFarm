package com.mapachos.pandoFarm.plants.engine.event.plant

import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.event.PlayerFarmEvent
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class HarvestPlantEvent(player: Player, val plant: HarvestPlant<out Entity>, val harvest: Harvest): PlayerFarmEvent(player)
