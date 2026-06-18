package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.engine.printTraceToConsole
import com.chattriggers.ctjs.internal.engine.JSLoader
import com.chattriggers.ctjs.internal.utils.getOrNull
import com.chattriggers.ctjs.internal.utils.toIdentifier
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.resources.Identifier
import net.minecraft.client.gui.components.toasts.Toast
import org.mozilla.javascript.Callable
import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.Undefined

import net.minecraft.client.renderer.RenderPipelines

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

// https://github.com/Edgeburn/Toasts
/**
 * Displays a toast in the top left corner similar to the MC advancement toast
 *
 * Object properties that can be passed to the constructor:
 * - title: A TextComponent (or anything that can be passed to the TextComponent constructor)
 * - description: A TextComponent (or anything that can be passed to the TextComponent constructor)
 * - background: An Image or a String/Identifier that points to a texture. Defaults to the advancement background
 * - icon: An Image or a String/Identifier that points to a texture
 * - width: The width of the toast, defaults to 160
 * - height: The height of the toast, defaults to 32
 * - displayTime: The time in ms the toast will be displayed, defaults to 5000
 * - render: An optional function that will be called to render the toast. By default, it renders the same
 *           way that advancement toasts do. If this function is called, it will not render anything by default.
 *           It takes no parameters and is called with the Toast object as its receiver.
 */
class Toast(config: NativeObject) : Toast {
    private var titleBacker: TextComponent? = null
    var title: Any?
        get() = titleBacker
        set(value) {
            titleBacker = value?.let { TextComponent(it) }
        }

    private var descriptionBacker: TextComponent? = null
    var description: Any?
        get() = descriptionBacker
        set(value) {
            descriptionBacker = value?.let { TextComponent(it) }
        }

    private var backgroundBacker: Identifier? = Identifier.withDefaultNamespace("toast/advancement")
    var background: Any?
        get() = backgroundBacker
        set(value) {
            backgroundBacker = toIdentifier(value)
        }

    private var iconBacker: Identifier? = null
    var icon: Any?
        get() = iconBacker
        set(value) {
            iconBacker = toIdentifier(value)
        }

    private var toastWidth = config.getOrNull("width")?.let {
        require(it is Number) { "Toast \"width\" must be a number" }
        it.toInt()
    } ?: super.width()

    private var toastHeight = config.getOrNull("height")?.let {
        require(it is Number) { "Toast \"height\" must be a number" }
        it.toInt()
    } ?: super.height()

    var displayTime = config.getOrNull("displayTime")?.let {
        require(it is Number) { "Toast \"displayTime\" must be a number" }
        it.toLong()
    } ?: 5000L

    private var customRenderFunction = config.getOrNull("render")?.let {
        check(it is Callable) { "Toast \"render\" function must be undefined or callable" }
        it
    }
    private val jsReceiver = if (customRenderFunction != null) {
        Context.javaToJS(this, Context.getContext().topCallScope) as Scriptable
    } else {
        null
    }

    private var startTime: Long? = null
    private var visibility: Toast.Visibility = Toast.Visibility.HIDE

    init {
        title = config.getOrNull("title")
        description = config.getOrNull("description")
        background = config.getOrDefault("background", backgroundBacker)
        icon = config.getOrNull("icon")
    }

    override fun width() = toastWidth
    override fun height() = toastHeight

    fun show() = apply {
        startTime = null
        //#if MC<26.2
        //$$Client.getMinecraft().toastManager.addToast(this)
        //#else
        Client.getMinecraft().gui.toastManager().addToast(this)
        //#endif
    }

    override fun getWantedVisibility(): Toast.Visibility = visibility

    override fun update(manager: ToastManager, time: Long) {
        if (startTime == null) {
            startTime = time
        }

        val duration = displayTime * (manager.notificationDisplayTimeMultiplier)
        val elapsed = time - startTime!!
        visibility = if (elapsed < duration) Toast.Visibility.SHOW else Toast.Visibility.HIDE
    }

    //#if MC<=12111
    //$$override fun render(
    //#else
    override fun extractRenderState(
    //#endif
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        textRenderer: Font, startTime: Long) {
        if (customRenderFunction != null) {
            GUIRenderer.withMatrix(UMatrixStack(drawContext.pose()).toMC()) {
                try {
                    JSLoader.invoke(customRenderFunction!!, emptyArray(), thisObj = jsReceiver!!)
                } catch (e: Throwable) {
                    e.printTraceToConsole()

                    // If the method threw, don't invoke it again
                    customRenderFunction = Callable { _, _, _, _ -> Undefined.instance }
                }
            }
        } else {
            backgroundBacker?.let {
                drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, it, 0, 0, width(), height())
            }

            iconBacker?.let { it: Identifier ->
                val iconSize = height() - ICON_PADDING * 2
                drawContext.blitSprite(RenderPipelines.GUI_TEXTURED, it, ICON_PADDING, ICON_PADDING, iconSize,iconSize)
            }

            val textX = if (icon == null) ICON_PADDING else height()
            var textY = ICON_PADDING

            titleBacker?.let {
                //#if MC<=12111
                //$$drawContext.drawString(
                //#else
                drawContext.text(
                //#endif
                    textRenderer, it, textX, textY, 0xFFFFFF, false
                )
                textY += textRenderer.lineHeight + 1
            }

            descriptionBacker?.let {
                //#if MC<=12111
                //$$drawContext.drawString(
                //#else
                drawContext.text(
                //#endif
                    textRenderer, it, textX, textY, 0xFFFFFF, false
                )
            }
        }
    }

    private companion object {
        private const val ICON_PADDING = 7

        private fun toIdentifier(value: Any?): Identifier? = when (value) {
            is Image -> value.getIdOrRegister()
            is CharSequence -> value.toString().toIdentifier()
            is Identifier -> value
            null -> null
            else -> throw IllegalArgumentException(
                "Toast \"background\" must be an Image or a string corresponding to a resource identifier",
            )
        }
    }
}
