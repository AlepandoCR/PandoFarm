package com.mapachos.pandoFarm.database.data

import org.bukkit.persistence.PersistentDataContainer

interface ContainerDto: Dto{
    fun applyOnPersistentDataContainer(persistentDataContainer: PersistentDataContainer)
}