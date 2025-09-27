package com.mapachos.pandoFarm.player.event

import com.mapachos.pandoFarm.player.data.PlayerDto
import com.mapachos.pandoFarm.plants.engine.event.FarmEvent

class PlayerLevelUpEvent(val player: PlayerDto) : FarmEvent() {
}