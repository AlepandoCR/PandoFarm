package com.mapachos.pandoFarm.plants

import com.mapachos.pandoFarm.plants.engine.InteractionMethod

enum class PlantType(val harvestMethod: InteractionMethod, val interactionMethod: InteractionMethod) {
    TOMATO_PLANT(InteractionMethod.DAMAGE, InteractionMethod.INTERACT),
}