package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.util.timer
import com.mapachos.pandoFarm.util.timerRunnable
import org.bukkit.scheduler.BukkitRunnable

class GrowthEngine(val plantRegistry: PlantRegistry) {
    val task: BukkitRunnable
    var ticks = 0
    init{
        task = runnable()
    }
    fun start() {
        task.timer(0,20)
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
                it.age++
            }

            ticks++
        }
    }
}