package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import net.kyori.adventure.text.Component
import org.bukkit.Material

enum class HarvestType(
    val customModelComponentString: String,
    val componentName: Component,
    val harvestEffectType: HarvestEffectType,
    val material: Material
) {
    TOMATO("pandofarm:tomato", Component.text("Tomato"), HarvestEffectType.NONE, Material.CARROT),
}