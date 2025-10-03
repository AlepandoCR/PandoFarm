package com.mapachos.pandoFarm.database.data.persistance

import com.mapachos.pandoFarm.PandoFarm
import org.bukkit.NamespacedKey

enum class DataNamespacedKey(val key: String) {
    YEAR("yearDto"),
    MONTH("monthDto"),
    DAY("dayDto"),
    SECOND("secondDto"),
    MINUTE("minuteDto"),
    HOUR("hourDto"),
    X("xDto"),
    Y("yDto"),
    Z("zDto"),
    WORLD("worldDto"),
    MATERIAL("materialDto"),
    QUALITY("qualityDto"),
    UUID("uuidDto"),
    PLANT_TYPE("plantTypeDto"),
    HARVEST_EFFECT_NAME("harvestEffectNameDto"),
    AGE("ageDto"),
    HARVEST_EFFECT_DESCRIPTION("descriptionDto"),
    LOCATION("locationDto"),
    HARVEST_TYPE_NAME("harvestTypeDto"),
    HARVEST_METHOD("harvestMethodDto"),
    INTERACTION_METHOD("interactionMethodDto"),
    MATURE_AGE("matureAgeDto"),
    ENTITY_CLASS("entityClassDto"),
    HARVEST_TYPE_CUSTOM_MODEL_COMPONENT_STRING("harvestTypeCustomModelComponentStringDto"),
    MODEL_BATCH("modelBatchDto"),
    HARVEST_NAME("harvestNameDto");


    fun toNamespacedKey(): NamespacedKey {
        return NamespacedKey(PandoFarm.getInstance(), key)
    }

    fun fromNamespacedKey(namespacedKey: NamespacedKey): DataNamespacedKey? {
        return entries.find { it.key == namespacedKey.key }
    }

    fun fromKey(key: String): DataNamespacedKey? {
        return entries.find { it.key == key }
    }
}