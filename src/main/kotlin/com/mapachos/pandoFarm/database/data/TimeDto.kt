package com.mapachos.pandoFarm.database.data

import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class TimeDto(
    val hour: Int,
    val minute: Int,
    val second: Int,
): Dto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.HOUR.toNamespacedKey(), PersistentDataType.INTEGER, hour)
        persistentDataContainer.set(DataNamespacedKey.MINUTE.toNamespacedKey(), PersistentDataType.INTEGER, minute)
        persistentDataContainer.set(DataNamespacedKey.SECOND.toNamespacedKey(), PersistentDataType.INTEGER, second)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): TimeDto? {
            val hour = persistentDataContainer.get(DataNamespacedKey.HOUR.toNamespacedKey(), PersistentDataType.INTEGER)
            val minute = persistentDataContainer.get(DataNamespacedKey.MINUTE.toNamespacedKey(), PersistentDataType.INTEGER)
            val second = persistentDataContainer.get(DataNamespacedKey.SECOND.toNamespacedKey(), PersistentDataType.INTEGER)
            if (hour == null || minute == null || second == null) return null
            return TimeDto(hour, minute, second)
        }
    }
}