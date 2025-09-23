package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto

data class HarvestDto(
    val material: String,
    val quality: Int,
    val harvestType: String
): Dto {
}
