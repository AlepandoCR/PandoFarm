package com.mapachos.pandoFarm.plants.engine.harvest

import net.kyori.adventure.text.Component

enum class HarvestType(
    val customModelComponentString: String,
    val componentName: Component
) {
    TOMATO("pandofarm:tomato", Component.text("Tomato")),
}