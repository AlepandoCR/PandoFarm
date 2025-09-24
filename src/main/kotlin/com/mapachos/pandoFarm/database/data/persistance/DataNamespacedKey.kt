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
    AGE("ageDto"),
    DESCRIPTION("descriptionDto"),
    LOCATION("locationDto"),
    HARVEST_TYPE("harvestTypeDto"),
    MODEL_BATCH("modelBatchDto");


    fun toNamespacedKey(): NamespacedKey {
        return NamespacedKey(PandoFarm.instance, key)
    }

    fun fromNamespacedKey(namespacedKey: NamespacedKey): DataNamespacedKey? {
        return entries.find { it.key == namespacedKey.key }
    }

    fun fromKey(key: String): DataNamespacedKey? {
        return entries.find { it.key == key }
    }
}