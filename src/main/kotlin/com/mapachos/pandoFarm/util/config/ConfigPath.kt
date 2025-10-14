package com.mapachos.pandoFarm.util.config

/**
 * Centralized config paths used across the plugin.
 */
enum class ConfigPath(val path: String) {
    // Growth engine
    GROWTH_TASK_PERIOD_TICKS("growth.task-period-ticks"),
    GROWTH_AGE_INCREMENT("growth.age-increment"),

    // Market
    MARKET_DEMAND_RECALC_PERIOD_TICKS("market.demand.recalc-period-ticks"),
    MARKET_DEMAND_MIN_MULTIPLIER("market.demand.min-multiplier"),
    MARKET_DEMAND_MAX_MULTIPLIER("market.demand.max-multiplier"),
    HARVEST_BASE_PRICE("market.demand.harvest-base-price"),

    // Player look engine
    LOOK_SCAN_RADIUS("look.scan-radius"),
    LOOK_PERIOD_TICKS("look.period-ticks");
}

