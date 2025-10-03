package com.mapachos.pandoFarm.market.engine

import com.mapachos.pandoFarm.market.data.SaleDto
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import java.util.*

data class Sale(
    val player: UUID,
    val harvest: Harvest,
    val price: Long,
    val amount: Double,
    val marketType: MarketType
) {
    init {
        require(amount > 0) { "Sale amount must be > 0" }
        require(price >= 0) { "Sale price must be >= 0" }
    }

    fun pricePerUnit(): Double = if (amount <= 0.0) 0.0 else price.toDouble() / amount

    fun toDto(): SaleDto = SaleDto(player, amount, price, harvest.toDto(), marketType.name)

    companion object {
        fun load(dto: SaleDto): Sale {
            return Sale(dto.uuid, dto.harvest.toHarvest(), dto.price, dto.amount, MarketType.valueOf(dto.marketType))
        }
    }
}