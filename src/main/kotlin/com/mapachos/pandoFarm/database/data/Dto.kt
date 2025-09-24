package com.mapachos.pandoFarm.database.data

import org.bukkit.persistence.PersistentDataContainer
import java.io.Serializable

interface Dto: Serializable{
    fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer)
}