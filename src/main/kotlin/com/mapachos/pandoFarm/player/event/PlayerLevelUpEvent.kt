package com.mapachos.pandoFarm.player.event

import com.mapachos.pandoFarm.plants.engine.event.FarmEvent
import com.mapachos.pandoFarm.player.data.PlayerDto

class PlayerLevelUpEvent(val player: PlayerDto) : FarmEvent()