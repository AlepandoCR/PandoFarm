package com.mapachos.pandoFarm.util.dialog

import com.mapachos.pandoFarm.PandoFarm
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.InlinedRegistryBuilderProvider
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.DialogInstancesProvider
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration

/**
 * AutoDialog is a wrapper around Paper's Dialog system.
 * It simplifies creating dialogs with buttons and callbacks.
 */
class AutoDialog private constructor(
    private val internal: Dialog
) {
    fun dialog(): Dialog = internal

    fun show(player: Player) {
        player.showDialog(internal)
    }

    companion object {
        fun builder(plugin: PandoFarm = PandoFarm.getInstance()): Builder = Builder(plugin)
    }

    class Builder(private val plugin: PandoFarm) {
        private var title: Component = Component.text("Dialog")
        private var externalTitle: Component? = null
        private var canCloseWithEscape: Boolean = true
        private var pause: Boolean = true
        private var afterAction: DialogBase.DialogAfterAction = DialogBase.DialogAfterAction.CLOSE

        private val body: MutableList<DialogBody> = mutableListOf()
        private val inputs: MutableList<DialogInput> = mutableListOf()
        private val buttons: MutableList<ActionButton> = mutableListOf()

        fun title(title: Component) = apply { this.title = title }
        fun externalTitle(externalTitle: Component?) = apply { this.externalTitle = externalTitle }
        fun canCloseWithEscape(value: Boolean) = apply { this.canCloseWithEscape = value }
        fun pause(value: Boolean) = apply { this.pause = value }
        fun afterAction(value: DialogBase.DialogAfterAction) = apply { this.afterAction = value }

        fun body(elements: List<DialogBody>) = apply { this.body += elements }
        fun addBody(element: DialogBody) = apply { this.body += element }

        fun inputs(elements: List<DialogInput>) = apply { this.inputs += elements }
        fun addInput(element: DialogInput) = apply { this.inputs += element }

        fun addButton(button: ActionButton) = apply { this.buttons += button }

        /**
         * Adds a custom-click button with callback.
         */
        fun button(
            label: Component,
            tooltip: Component? = null,
            expireAfter: Duration = Duration.ofMinutes(5),
            maxUses: Int = 1,
            handler: (Context) -> Unit
        ) = apply {
            val options = ClickCallback.Options.builder()
                .lifetime(expireAfter)
                .uses(maxUses)
                .build()

            val callback = DialogActionCallback { response, audience ->
                val player = audience as? Player ?: return@DialogActionCallback
                val ctx = Context(player, audience, response)
                handler(ctx)
            }

            val action = DialogInstancesProvider.instance().register(callback, options)

            val btn = ActionButton.builder(label)
                .apply { tooltip?.let { tooltip(it) } }
                .action(action)
                .build()

            this.buttons += btn
        }

        /**
         * Adds a command-template action button.
         */
        fun commandButton(
            label: Component,
            tooltip: Component? = null,
            commandTemplate: String
        ) = apply {
            val action = DialogAction.commandTemplate(commandTemplate)
            val btn = ActionButton.builder(label)
                .apply { tooltip?.let { tooltip(it) } }
                .action(action)
                .build()
            this.buttons += btn
        }

        /**
         * Adds a static action button.
         */
        fun staticButton(
            label: Component,
            tooltip: Component? = null,
            static: DialogAction.StaticAction
        ) = apply {
            val btn = ActionButton.builder(label)
                .apply { tooltip?.let { tooltip(it) } }
                .action(static)
                .build()
            this.buttons += btn
        }

        fun build(): Dialog {
            val baseBuilder = DialogBase.builder(title)
                .canCloseWithEscape(canCloseWithEscape)
                .pause(pause)
                .afterAction(afterAction)

            externalTitle?.let { baseBuilder.externalTitle(it) }
            if (body.isNotEmpty()) baseBuilder.body(body)
            if (inputs.isNotEmpty()) baseBuilder.inputs(inputs)

            return InlinedRegistryBuilderProvider.instance().createDialog { factory ->
                val builder = factory.empty()

                builder.base(baseBuilder.build())

                if (buttons.isNotEmpty()) {
                    // uses multi action bc it's the common case
                    builder.type(DialogType.multiAction(buttons).build())
                } else {
                    builder.type(DialogInstancesProvider.instance().notice())
                }
            }
        }
    }

    data class Context(
        val player: Player,
        val audience: Audience,
        val response: DialogResponseView
    )
}