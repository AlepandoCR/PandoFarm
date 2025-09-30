package com.mapachos.pandoFarm.plants.engine.event

import org.bukkit.entity.Player

abstract class PlayerFarmEvent(val player: Player) : FarmEvent()