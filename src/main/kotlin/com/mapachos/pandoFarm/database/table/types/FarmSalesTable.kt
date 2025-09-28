package com.mapachos.pandoFarm.database.table.types

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.AutoTable
import com.mapachos.pandoFarm.market.data.SaleDto

class FarmSalesTable(
    mysql: MySQLManager,
) : AutoTable<SaleDto>(mysql, SaleDto::class, "farm_market_sales", "uuid")