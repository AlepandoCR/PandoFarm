package com.mapachos.pandoFarm.database.data

import org.bukkit.persistence.PersistentDataContainer
import java.time.LocalDateTime

data class TimeStampDto(val time: TimeDto, val date: DateDto): ContainerDto {
    override fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer) {
        time.applyOnPersistentDataContainer(persistentDataContainer)
        date.applyOnPersistentDataContainer(persistentDataContainer)
    }

    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(
            date.year,
            date.month,
            date.day,
            time.hour,
            time.minute,
            time.second
        )
    }

    companion object {
        fun fromPersistentDataContainer(persistentDataContainer: PersistentDataContainer): TimeStampDto? {
            val time = TimeDto.fromPersistentDataContainer(persistentDataContainer)
            val date = DateDto.fromPersistentDataContainer(persistentDataContainer)
            if (time == null || date == null) return null
            return TimeStampDto(time, date)
        }

        fun now(): TimeStampDto {
            val now = LocalDateTime.now()
            val time = TimeDto(now.hour, now.minute, now.second)
            val date = DateDto(now.year, now.monthValue, now.dayOfMonth)
            return TimeStampDto(time, date)
        }
    }
}
