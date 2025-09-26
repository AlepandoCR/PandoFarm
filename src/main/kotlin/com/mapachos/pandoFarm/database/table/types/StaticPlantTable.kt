package com.mapachos.pandoFarm.database.table.types

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.AutoTable
import com.mapachos.pandoFarm.plants.data.StaticPlantDto

class StaticPlantTable(
    mysql: MySQLManager,
) : AutoTable<StaticPlantDto>(mysql, StaticPlantDto::class, "static_plants", "uniqueIdentifier")