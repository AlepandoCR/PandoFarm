package com.mapachos.pandoFarm.economy.market.ui

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.economy.EconomyController
import com.mapachos.pandoFarm.economy.market.engine.FarmMarketManager
import com.mapachos.pandoFarm.economy.market.engine.MarketType
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestType
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import com.mapachos.pandoFarm.util.dialog.AutoDialog
import com.mapachos.pandoFarm.util.giveItem
import com.mapachos.pandoFarm.util.tryExtractingHarvest
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import java.time.Duration
import kotlin.math.roundToInt

class MarketMenu(
    val player: Player,
    val harvestType: HarvestType,
    val plugin: PandoFarm
) {
    val harvestItem = harvestType.buildHarvest(6).harvestItem.buildItem()

    val mainMenuDialog = buildMainMenuDialog()

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

            addInput(DialogInput
                .numberRange("harvest_sell_amount",
                    Component.text("Cantidad a vender"),
                    1f,100f
                ).step(1f).initial(1f).build())

            addButton(
                ActionButton
                    .builder(Component.text("Vender"))
                    .action(DialogAction.customClick({ response, audience ->
                        if(audience is Player){
                            val toSell = response.getFloat("harvest_sell_amount")?.roundToInt() ?: 0
                            if(audience.tryExtractingHarvest(toSell,harvestType)){
                                FarmMarketManager.getMarketByType(MarketType.MAIN)?.addSale(audience, harvestType.buildHarvest(), toSell.toDouble())
                                audience.sendMessage("Vendidos $toSell ${harvestType.name}.")
                            } else {
                                audience.sendMessage("No tienes suficiente ${harvestType.name}.")
                            }
                        }
                    }, ClickCallback.Options.builder().uses(1).lifetime(Duration.ZERO).build())).build()


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
            addInput(DialogInput
                .numberRange("harvest_buy_amount",
                    Component.text("Cantidad a comprar"),
                    1f,64f
                ).step(1f).initial(1f).build())
            addButton(
                ActionButton
                    .builder(Component.text("Comprar"))
                    .action(DialogAction.customClick({ response, audience ->
                        if(audience is Player){
                            val logger = plugin.logger
                            logger.info("[MarketMenu] Click en Comprar por $audience.name para ${harvestType.name}")
                            val toBuy = response.getFloat("harvest_buy_amount")?.roundToInt() ?: 0
                            logger.info("[MarketMenu] Cantidad solicitada: $toBuy")
                            if (toBuy <= 0){
                                audience.sendMessage("Cantidad inválida.")
                                logger.info("[MarketMenu] Abortado: cantidad <= 0")
                                return@customClick
                            }
                            val price = FarmMarketManager.getMarketByType(MarketType.MAIN)?.getPrice(harvestType)?.toDouble() ?: 0.0
                            val total = price * toBuy
                            val econReady = EconomyController.isReady()
                            val balanceBefore = EconomyController.getBalance(audience)
                            logger.info("[MarketMenu] Precio unitario: $price, Total: $total, EconReady=$econReady, BalanceAntes=$balanceBefore")

                            if(!econReady){
                                audience.sendMessage("Economía no disponible.")
                                logger.info("[MarketMenu] Abortado: Economía no lista")
                                return@customClick
                            }

                            val hasMoney = EconomyController.has(audience, total)
                            logger.info("[MarketMenu] Tiene fondos suficientes: $hasMoney")
                            if(!hasMoney){
                                audience.sendMessage("Fondos insuficientes. Necesitas ${EconomyController.format(total)}")
                                return@customClick
                            }

                            val withdrawn = EconomyController.withdraw(audience, total)
                            logger.info("[MarketMenu] Retiro de fondos resultado=$withdrawn")
                            if(!withdrawn){
                                audience.sendMessage("No se pudo procesar el pago.")
                                return@customClick
                            }

                            val plantTypes = PlantTypeRegistry.getPlantTypesByHarvestType(harvestType)
                            logger.info("[MarketMenu] PlantTypes asociados a ${harvestType.name}: ${plantTypes.size} de ${PlantTypeRegistry.plants.size}" )
                            val seeds = mutableListOf<Seed<out Material>>()
                            plantTypes.forEach {
                                seeds.add(Seed(Material.COOKIE,it.toDto()))
                            }

                            seeds.forEach { seed ->
                                audience.giveItem(seed.buildItem(), toBuy)
                            }
                            val balanceAfter = EconomyController.getBalance(audience)
                            logger.info("[MarketMenu] Compra completada. Items entregados: ${seeds.size} tipos x $toBuy c/u. BalanceDespués=$balanceAfter")
                            audience.sendMessage("Compra realizada por ${EconomyController.format(total)}.")
                        }
                    }, ClickCallback.Options.builder().uses(-1).lifetime(Duration.ofMinutes(10)).build())).build()


            )
        }
        return builder.build()
    }


}