package com.mapachos.pandoFarm.market.engine

import com.mapachos.pandoFarm.PandoFarm

object FarmMarketManager {

    val markets = mutableListOf<FarmMarket>()

    fun registerMarket(market: FarmMarket){
        if(!containsType(market.marketType)) markets.add(market)
    }

    fun containsType(marketType: MarketType): Boolean {
        return markets.any { it.marketType == marketType }
    }

    fun unregisterMarket(market: FarmMarket){
        markets.remove(market)
    }

    fun clearMarkets(){
        markets.clear()
    }

    fun getMarketByType(marketType: MarketType): FarmMarket? {
        return markets.firstOrNull { it.marketType == marketType }
    }

    fun loadAllMarkets(plugin: PandoFarm){
        clearMarkets()
        MarketType.entries.forEach { marketType ->
            val market = FarmMarket.loadFarmMarket(plugin, marketType)
            registerMarket(market)
        }
    }

    fun saveAllMarkets(plugin: PandoFarm){
        markets.forEach { market ->
            market.sales.forEach { sale ->
                plugin.getFarmSalesTable().insertOrUpdate(sale.toDto())
            }
        }
    }

    fun reloadDemand(plugin: PandoFarm){
        val minMult = plugin.config.getDouble("market.demand.min-multiplier", 0.5)
        val maxMult = plugin.config.getDouble("market.demand.max-multiplier", 2.0)
        markets.forEach { it.recalculateDemandAdjustments(minMult, maxMult) }

    }
}