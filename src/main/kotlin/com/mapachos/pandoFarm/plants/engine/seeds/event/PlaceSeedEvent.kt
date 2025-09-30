package com.mapachos.pandoFarm.plants.engine.seeds.event

import com.mapachos.pandoFarm.plants.engine.event.PlayerFarmEvent
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import org.bukkit.Material
import org.bukkit.entity.Player

class PlaceSeedEvent(player: Player ,val seed: Seed<out Material>) : PlayerFarmEvent(player)