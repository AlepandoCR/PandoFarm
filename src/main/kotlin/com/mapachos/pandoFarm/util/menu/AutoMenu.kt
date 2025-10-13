package com.mapachos.pandoFarm.util.menu

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.util.listeners.DynamicListener
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * AutoMenu acts like a virtual paged inventory backed by a MutableList<ItemStack>.
 * - Provides full List API (add/remove/clear/etc.).
 * - play(player): opens a dynamic-sized inventory, reserving the last row for arrows when pagination is needed.
 */
class AutoMenu(
    private val plugin: PandoFarm,
    private val title: Component = Component.text("Menu"),
    private val items: MutableList<ItemStack> = mutableListOf(),
    val task: (InventoryClickEvent) -> Unit = {}
) : AbstractMutableList<ItemStack>() {

    override val size: Int
        get() = items.size

    override fun get(index: Int): ItemStack = items[index]

    override fun set(index: Int, element: ItemStack): ItemStack = items.set(index, element)

    override fun add(index: Int, element: ItemStack) {
        items.add(index, element)
    }

    override fun removeAt(index: Int): ItemStack = items.removeAt(index)

    override fun clear() {
        items.clear()
    }

    override fun iterator(): MutableIterator<ItemStack> = items.iterator()

    /** Opens the menu for the given player. */
    fun play(player: Player) {
        val totalItems = items.size
        // Determine paging needs
        val singlePage = totalItems <= 54

        // Snapshot items to avoid concurrent modifications during play
        val snapshot = items.toList()

        // Layout config
        val rows: Int
        val hasPagination: Boolean
        val itemsPerPage: Int
        if (singlePage) {
            rows = max(1, min(6, ceil(max(1, totalItems) / 9.0).toInt()))
            hasPagination = false
            itemsPerPage = rows * 9
        } else {
            rows = 6 // 5 rows for items + 1 row for arrows
            hasPagination = true
            itemsPerPage = 45
        }
        val totalPages = if (hasPagination) ceil(totalItems / itemsPerPage.toDouble()).toInt() else 1

        val dynamic = DynamicListener()

        var currentPage = 0
        var switchingPage = false
        var currentInventory: Inventory? = null

        val prevArrow = makeArrow("Previous")
        val nextArrow = makeArrow("Next")

        fun openPage(page: Int) {
            val safePage = page.coerceIn(0, totalPages - 1)
            val invSize = rows * 9
            val pageTitle = if (hasPagination) title.append(Component.text("(${safePage + 1}/$totalPages)")) else title
            val inventory = Bukkit.createInventory(null, invSize,pageTitle)

            val start = safePage * itemsPerPage
            val endExclusive = min(start + itemsPerPage, snapshot.size)
            var slot = 0
            for (i in start until endExclusive) {
                inventory.setItem(slot, snapshot[i].clone())
                slot++
            }
            if (hasPagination) {
                val base = invSize - 9
                if (safePage > 0) inventory.setItem(base, prevArrow)
                if (safePage < totalPages - 1) inventory.setItem(invSize - 1, nextArrow)
            }
            switchingPage = true
            currentInventory = inventory
            player.openInventory(inventory)
            switchingPage = false
        }

        val listener = object : Listener {
            @EventHandler(priority = EventPriority.HIGHEST)
            fun onClick(event: InventoryClickEvent) {
                val inventory = currentInventory ?: return
                if (event.view.topInventory != inventory) return
                // Block all interactions (both top and bottom) while menu is open
                event.isCancelled = true

                // Only handle arrow clicks occurring in the top inventory's last row
                val raw = event.rawSlot
                val topSize = inventory.size
                if (raw !in 0..<topSize) return // clicked bottom inventory

                if (hasPagination && raw >= topSize - 9) {
                    val clicked = event.currentItem ?: return
                    if (clicked.isSimilar(prevArrow) && currentPage > 0) {
                        currentPage -= 1
                        openPage(currentPage)
                        return
                    } else if (clicked.isSimilar(nextArrow) && currentPage < totalPages - 1) {
                        currentPage += 1
                        openPage(currentPage)
                        return
                    }
                }
                task(event)
            }

            @EventHandler(priority = EventPriority.MONITOR)
            fun onClose(event: InventoryCloseEvent) {
                val inventory = currentInventory ?: return
                if (event.view.topInventory != inventory) return
                if (switchingPage) return
                dynamic.close()
            }
        }

        dynamic.setListener(listener)
        dynamic.start()
        openPage(0)
    }

    fun makeArrow(name: String): ItemStack {
        val stack = ItemStack(Material.ARROW, 1)
        val meta = stack.itemMeta
        meta.displayName(Component.text(name))
        stack.itemMeta = meta
        return stack
    }

}
