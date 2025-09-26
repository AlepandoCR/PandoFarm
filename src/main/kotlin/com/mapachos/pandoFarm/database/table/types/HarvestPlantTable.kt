package com.mapachos.pandoFarm.database.table.types

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.AutoTable
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto

class HarvestPlantTable(
    mysql: MySQLManager,
) : AutoTable<HarvestPlantDto>(mysql, HarvestPlantDto::class, "harvest_plants", "uniqueIdentifier")