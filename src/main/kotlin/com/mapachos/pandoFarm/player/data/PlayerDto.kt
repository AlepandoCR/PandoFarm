package com.mapachos.pandoFarm.player.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.player.event.PlayerLevelUpEvent

data class PlayerDto(
    val uuid: String,
    var harvestedPlants: Long,
    var plantedPlants: Long,
    var harvestEarnings: Long,
    private var farmerExp: Long = 0L,
): Dto {

    /**
     * Calculates the farming level based on the current farming experience.
     * Uses a quadratic progression: exp needed for level n = base * n^2
     * This makes early levels easy and higher levels progressively harder.
     */
    fun getFarmerLevel(): Int {
        val base = 100 // Base experience required for level 1
        var level = 1
        var expNeeded = base
        var expLeft = farmerExp
        while (expLeft >= expNeeded) {
            expLeft -= expNeeded
            level++
            expNeeded = base * level * level
        }
        return level
    }

    /**
     * Returns the experience required to reach the next farming level.
     */
    fun getExpToNextLevel(): Long {
        val nextLevel = getFarmerLevel() + 1
        val base = 100
        var totalExp = 0L
        for (lvl in 1 until nextLevel) {
            totalExp += base * lvl * lvl
        }
        return totalExp - farmerExp
    }

    fun addExp(amount: Long) {
        if (amount <= 0) return
        val oldLevel = getFarmerLevel()
        farmerExp += amount
        val newLevel = getFarmerLevel()
        if (newLevel > oldLevel) {
            PlayerLevelUpEvent(this).callEvent()
        }
    }

    fun getExp(): Long {
        return farmerExp
    }

    companion object{
        fun create(uuid: String): PlayerDto {
            return PlayerDto(uuid, 0, 0, 0, 0)
        }
    }
}