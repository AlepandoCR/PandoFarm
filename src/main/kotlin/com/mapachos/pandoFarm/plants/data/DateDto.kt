package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto

data class DateDto(
    val year: Int,
    val month: Int,
    val day: Int,
) : Dto {
}