package com.mapachos.gachapon.engine.item

import com.google.common.collect.Multimap
import com.mapachos.gachapon.quality.GachaponQuality
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import org.bukkit.persistence.PersistentDataContainer

abstract class GachaponItem {

    private lateinit var serializedItem: ByteArray

    protected abstract fun material(): Material

    protected abstract fun quality(): GachaponQuality

    protected abstract fun name(): Component

    protected abstract fun lore(): List<Component>

    protected abstract fun persistentDataContainer(persistentDataContainer: PersistentDataContainer)

    protected abstract fun enchantments(): Map<Enchantment, Int>

    protected abstract fun customModelDataComponent(customModelDataComponent: CustomModelDataComponent)

    protected abstract fun attributeModifiers(map: Multimap<Attribute, AttributeModifier>?)

    protected abstract fun addAttributeModifier(): Map<Attribute,AttributeModifier>

    fun buildItem(): ItemStack{
        val item = ItemStack(material())
        val meta = item.itemMeta ?: return item
        decorators(meta)
        data(meta)

        return item
    }

    fun data(meta: ItemMeta) {
        persistentDataContainer(meta.persistentDataContainer)
        enchantments().forEach { enchantment ->
            meta.addEnchant(enchantment.key, enchantment.value, true)
        }
        customModelDataComponent(meta.customModelDataComponent)
        addAttributeModifier().forEach { meta.addAttributeModifier(it.key,it.value) }
        attributeModifiers(meta.attributeModifiers)
    }

    private fun decorators(meta: ItemMeta) {
        meta.displayName(name())
        meta.lore(lore())
        meta.lore()?.add(Component.text(quality().name).color(quality().color))
    }

    fun bytes(): ByteArray{
        if(!::serializedItem.isInitialized){
            serializedItem = buildItem().serializeAsBytes()
        }
        return serializedItem
    }
}