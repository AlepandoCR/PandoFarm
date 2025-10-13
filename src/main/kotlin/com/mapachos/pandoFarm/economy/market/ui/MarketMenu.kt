package com.mapachos.pandoFarm.economy.market.ui

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestType
import com.mapachos.pandoFarm.util.dialog.AutoDialog
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player

class MarketMenu(
    val player: Player,
    val harvestType: HarvestType,
    val plugin: PandoFarm
) {
    val mainMenuDialog = buildMainMenuDialog()

    val harvestItem = harvestType.buildHarvest(6).harvestItem.buildItem()

    fun buildMainMenuDialog(): Dialog {
        val builder = AutoDialog.builder(plugin)
        builder.apply {
            title(Component.text("Compra y Venta de ${harvestType.name}"))
            canCloseWithEscape(true)
            addBody(DialogBody.item(harvestItem,null,true,true,10,10))
            addButton(
                ActionButton
                    .builder(Component.text("Comprar"))
                    .action(
                        DialogAction.staticAction(
                            ClickEvent.showDialog(buildBuyMenuDialog()
                            )
                        )
                    ).build()
            )

            addButton(
                ActionButton
                    .builder(Component.text("Vender"))
                    .action(
                        DialogAction.staticAction(
                            ClickEvent.showDialog(buildSellMenuDialog()
                            )
                        )
                    ).build()
            )
        }
        return builder.build()
    }

    fun buildSellMenuDialog(): Dialog {
        val builder = AutoDialog.builder(plugin)
        builder.apply {
            title(Component.text("Vender ${harvestType.name}"))
            canCloseWithEscape(true)
            addBody(DialogBody.item(harvestItem,null,true,true,10,10))
            addButton(
                ActionButton
                    .builder(Component.text("Vender"))
                    .action(DialogAction.staticAction(ClickEvent.callback {audience ->

                    })).build()
            )
        }
        return builder.build()
    }

    fun buildBuyMenuDialog(): Dialog {
        val builder = AutoDialog.builder(plugin)
        builder.apply {
            title(Component.text("Comprar ${harvestType.name}"))
            canCloseWithEscape(true)
            addBody(DialogBody.item(harvestItem,null,true,true,10,10))
            addButton(
                ActionButton
                    .builder(Component.text("Comprar"))
                    .action(DialogAction.staticAction(ClickEvent.callback {audience ->

                    })).build()
            )
        }
        return builder.build()
    }


}