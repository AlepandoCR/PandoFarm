package com.mapachos.pandoFarm.plants.engine.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class PlayerFarmEvent(val player: Player) : FarmEvent()