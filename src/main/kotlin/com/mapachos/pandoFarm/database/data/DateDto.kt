package com.mapachos.pandoFarm.database.data

import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

data class DateDto(
    val year: Int,
    val month: Int,
    val day: Int,
) : Dto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        persistentDataContainer.set(DataNamespacedKey.YEAR.toNamespacedKey(), PersistentDataType.INTEGER, year)
        persistentDataContainer.set(DataNamespacedKey.MONTH.toNamespacedKey(), PersistentDataType.INTEGER, month)
        persistentDataContainer.set(DataNamespacedKey.DAY.toNamespacedKey(), PersistentDataType.INTEGER, day)
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): DateDto? {
            val year = persistentDataContainer.get(DataNamespacedKey.YEAR.toNamespacedKey(), PersistentDataType.INTEGER)
            val month = persistentDataContainer.get(DataNamespacedKey.MONTH.toNamespacedKey(), PersistentDataType.INTEGER)
            val day = persistentDataContainer.get(DataNamespacedKey.DAY.toNamespacedKey(), PersistentDataType.INTEGER)
            if (year == null || month == null || day == null) return null
            return DateDto(year, month, day)
        }
    }
}