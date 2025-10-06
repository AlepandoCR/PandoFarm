package com.mapachos.pandoFarm.market.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.util.config.ConfigPath

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
        val minMult = plugin.config.getDouble(ConfigPath.MARKET_DEMAND_MIN_MULTIPLIER.path, 0.5)
        val maxMult = plugin.config.getDouble(ConfigPath.MARKET_DEMAND_MAX_MULTIPLIER.path, 2.0)
        markets.forEach { it.recalculateDemandAdjustments(minMult, maxMult) }

    }
}