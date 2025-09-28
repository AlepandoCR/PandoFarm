package com.mapachos.pandoFarm.market.engine

import com.mapachos.pandoFarm.PandoFarm

class FarmMarket(val plugin: PandoFarm, val marketType: MarketType) {

    val sales = mutableListOf<Sale>()

    companion object{
        fun loadFarmMarket(plugin: PandoFarm, marketType: MarketType): FarmMarket {
            val market = FarmMarket(plugin, marketType)

            market.sales.addAll(
                plugin.getFarmSalesTable().getAll().filter{
                    it.marketType == marketType.name
                }.map {
                    it.toSale()
                }
            )

            return market
        }
    }
}