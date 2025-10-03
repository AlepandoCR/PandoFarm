package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.util.timer
import com.mapachos.pandoFarm.util.timerRunnable
import org.bukkit.scheduler.BukkitRunnable

class GrowthEngine(val plantRegistry: PlantRegistry) {
    val task: BukkitRunnable
    var ticks = 0
    private val plugin get() = plantRegistry.plugin

    private val taskPeriod: Long get() = plugin.config.getLong("growth.task-period-ticks").takeIf { it > 0 } ?: 20L
    private val ageIncrement: Long get() = plugin.config.getLong("growth.age-increment").takeIf { it > 0 } ?: 1L

    init{
        task = runnable()
    }
    fun start() {
        task.timer(0, taskPeriod)
    }

    fun stop() {
        task.cancel()
    }

    private fun runnable(): BukkitRunnable {
        return timerRunnable {
            plantRegistry.registry.forEach {
                if(ticks % it.growInterval() == 0L) {
                    it.grow()
                }
                // Overflow-safe increment
                val newAge = it.age + ageIncrement
                it.age = if(newAge < 0) it.matureAge else newAge
            }

            ticks++
        }
    }
}