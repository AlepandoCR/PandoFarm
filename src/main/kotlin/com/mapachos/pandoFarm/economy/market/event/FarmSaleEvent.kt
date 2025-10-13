package com.mapachos.pandoFarm.economy.market.event

import com.mapachos.pandoFarm.economy.market.engine.Sale
import com.mapachos.pandoFarm.plants.engine.event.PlayerFarmEvent
import org.bukkit.entity.Player

class FarmSaleEvent(player: Player, val sale: Sale): PlayerFarmEvent(player)
