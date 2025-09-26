package com.mapachos.pandoFarm.plants.engine.harvest

object HarvestTypeRegistry {
    val harvests = mutableListOf<Harvest>()

    fun registerHarvest(harvest: Harvest) {
        harvests.add(harvest)
    }

    fun unregisterHarvest(harvest: Harvest) {
        harvests.remove(harvest)
    }

    fun getHarvestByType(type: HarvestType): Harvest? {
        return harvests.firstOrNull { it.harvestType == type }
    }
}