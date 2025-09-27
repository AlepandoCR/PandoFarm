package com.mapachos.pandoFarm.database.table.types

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.AutoTable
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto
import com.mapachos.pandoFarm.player.data.PlayerDto

class PlayerDataTable(
    mysql: MySQLManager,
) : AutoTable<PlayerDto>(mysql, PlayerDto::class, "player_data", "uuid")