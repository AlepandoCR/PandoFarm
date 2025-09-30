package com.mapachos.pandoFarm.plants.engine.seeds

import com.google.common.collect.Multimap
import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.data.PlantTypeDto
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class Seed<I: Material>(
    val material: I,
    val plant: PlantTypeDto
) {


    private lateinit var serializedItem: ByteArray

    private fun lore(): List<Component>{
        return listOf(
            Component.text("Plant: ${plant.plantTypeName}"),
        )
    }

    private fun persistentDataContainer(persistentDataContainer: PersistentDataContainer){
        plant.applyOnPersistentDataContainer(persistentDataContainer)
        persistentDataContainer.set(commonSeedNamespace, PersistentDataType.BYTE, 1)
    }

    private fun customModelDataComponent(customModelDataComponent: CustomModelDataComponent){
        customModelDataComponent.strings.add(plant.plantTypeName.replace(" ", "_").lowercase())
    }

    private fun attributeModifiers(map: Multimap<Attribute, AttributeModifier>?){

    }

    fun buildItem(): ItemStack{
        val item = ItemStack(material)
        val meta = item.itemMeta ?: return item
        decorators(meta)
        data(meta)

        return item
    }

    fun data(meta: ItemMeta) {
        persistentDataContainer(meta.persistentDataContainer)
        customModelDataComponent(meta.customModelDataComponent)
        attributeModifiers(meta.attributeModifiers)
    }

    private fun decorators(meta: ItemMeta) {
        val plantName = plant.plantTypeName.replace("_", " ")
        meta.displayName(Component.text("$plantName Seed"))
        meta.lore(lore())
    }

    fun plantSeed(location: Location, gardener: Entity){
        val plant = this.plant.createPlant(location, gardener)
        plant.spawn(location)
    }

    fun bytes(): ByteArray{
        if(!::serializedItem.isInitialized){
            serializedItem = buildItem().serializeAsBytes()
        }
        return serializedItem
    }

    companion object{
        private val commonSeedNamespace = NamespacedKey(PandoFarm.getInstance(), "seed")

        fun isSeed(item: ItemStack): Boolean{
            val meta = item.itemMeta ?: return false
            return meta.persistentDataContainer.has(commonSeedNamespace, PersistentDataType.BYTE)
        }

        fun fromItem(item: ItemStack): Seed<Material>?{
            if(!isSeed(item)) return null
            val meta = item.itemMeta ?: return null
            val plant = PlantTypeDto.fromPersistentDataContainer(meta.persistentDataContainer) ?: return null
            return Seed(item.type, plant)
        }
    }
}