package com.mapachos.pandoFarm.economy.market.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.economy.market.engine.MarketType
import com.mapachos.pandoFarm.economy.market.engine.Sale
import com.mapachos.pandoFarm.plants.engine.harvest.data.HarvestDto
import java.util.*

data class SaleDto(
    val uuid: UUID,
    val amount: Double,
    val price: Long,
    val harvest: HarvestDto,
    val marketType: String,
): Dto{

    fun toSale(): Sale {
        return Sale(uuid, harvest.toHarvest(), price, amount , MarketType.valueOf(marketType))
    }
}
