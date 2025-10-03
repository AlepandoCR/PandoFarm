package com.mapachos.pandoFarm.market.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.market.event.FarmSaleEvent
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class FarmMarket(val plugin: PandoFarm, val marketType: MarketType) {

    val sales = mutableListOf<Sale>()

    private val demandCounter = mutableMapOf<String, Double>() // harvestTypeName -> amount
    private val priceMultipliers = mutableMapOf<String, Double>()

    fun addSale(player: Player, harvest: Harvest, amount: Double, unitBasePrice: Long): Sale {
        val totalPriceRaw = unitBasePrice * amount
        val safePrice = if(totalPriceRaw > Long.MAX_VALUE) Long.MAX_VALUE else totalPriceRaw.toLong()
        val sale = Sale(player.uniqueId, harvest, safePrice, amount, marketType)
        sales.add(sale)
        // persist immediately (can later batch if needed)
        plugin.getFarmSalesTable().insertOrUpdate(sale.toDto())
        // update demand
        val typeName = harvest.harvestType.name
        demandCounter[typeName] = (demandCounter[typeName] ?: 0.0) + amount
        // fire event
        FarmSaleEvent(player, sale).callEvent()


        return sale
    }

    fun recalculateDemandAdjustments(minMult: Double, maxMult: Double){
        if(demandCounter.isEmpty()) return
        val total = demandCounter.values.sum()
        if(total <= 0) return
        val avgShare = total / demandCounter.size
        demandCounter.forEach { (type, amount) ->
            val shareFactor = if(amount <= 0) maxMult else avgShare / amount // high amount -> <1 low amount -> >1
            val clamped = min(max(shareFactor, minMult), maxMult)
            priceMultipliers[type] = clamped
        }
        // reset period counters
        demandCounter.clear()
    }

    fun getDynamicUnitPrice(basePrice: Long, harvestTypeName: String): Long {
        val multipliers = priceMultipliers[harvestTypeName] ?: 1.0
        val adjusted = (basePrice * multipliers).toLong()
        return when {
            adjusted < 0 -> 0
            else -> adjusted
        }
    }

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