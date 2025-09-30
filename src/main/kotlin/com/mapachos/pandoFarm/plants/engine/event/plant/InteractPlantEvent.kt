package com.mapachos.pandoFarm.plants.engine.event.plant

import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.event.PlayerFarmEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class InteractPlantEvent(player: Player, val plant: Plant<out Entity>): PlayerFarmEvent(player)