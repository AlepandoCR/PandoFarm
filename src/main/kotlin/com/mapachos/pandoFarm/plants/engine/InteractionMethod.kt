package com.mapachos.pandoFarm.plants.engine

enum class InteractionMethod() {
    RIGHT_CLICK,
    DAMAGE;

    companion object {
        fun fromString(name: String): InteractionMethod? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}