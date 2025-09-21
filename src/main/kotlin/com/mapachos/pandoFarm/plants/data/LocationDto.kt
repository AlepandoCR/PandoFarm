package com.mapachos.pandoFarm.plants.data

import org.bukkit.Location
import org.bukkit.World

data class LocationDto(
    val x: Double,
    val y: Double,
    val z: Double,
) {

    fun toLocation(world: World): Location {
        return Location(world, x, y, z)
    }

    companion object {
        fun Location.toDto() = LocationDto(x, y, z)
    }
}