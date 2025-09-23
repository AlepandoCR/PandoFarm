package com.mapachos.pandoFarm.database.data

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

data class LocationDto(
    val x: Double,
    val y: Double,
    val z: Double,
    val world: String
) {

    fun toLocation(): Location {
        val world = Bukkit.getWorld(world)
        return Location(world, x, y, z)
    }

    companion object {
        fun Location.toDto() = LocationDto(x, y, z, world.name)
    }
}