package com.mapachos.pandoFarm.database.data

import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class LocationDto(
    val x: Double,
    val y: Double,
    val z: Double,
    val world: String
): Dto {

    fun toLocation(): Location {
        val world = Bukkit.getWorld(world)
        return Location(world, x, y, z)
    }

    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.X.toNamespacedKey(), PersistentDataType.DOUBLE, x)
        persistentDataContainer.set(DataNamespacedKey.Y.toNamespacedKey(), PersistentDataType.DOUBLE, y)
        persistentDataContainer.set(DataNamespacedKey.Z.toNamespacedKey(), PersistentDataType.DOUBLE, z)
        persistentDataContainer.set(DataNamespacedKey.WORLD.toNamespacedKey(), PersistentDataType.STRING, world)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): LocationDto? {
            val x = persistentDataContainer.get(DataNamespacedKey.X.toNamespacedKey(), PersistentDataType.DOUBLE)
            val y = persistentDataContainer.get(DataNamespacedKey.Y.toNamespacedKey(), PersistentDataType.DOUBLE)
            val z = persistentDataContainer.get(DataNamespacedKey.Z.toNamespacedKey(), PersistentDataType.DOUBLE)
            val world = persistentDataContainer.get(DataNamespacedKey.WORLD.toNamespacedKey(), PersistentDataType.STRING)
            if (x == null || y == null || z == null || world == null) return null
            return LocationDto(x, y, z, world)
        }

        fun Location.toDto() = LocationDto(x, y, z, world.name)
    }
}