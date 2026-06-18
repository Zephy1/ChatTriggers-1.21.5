package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.KeyModifiers
import com.chattriggers.ctjs.api.client.Settings
import com.chattriggers.ctjs.api.client.toInt
import com.chattriggers.ctjs.api.client.toModifiers
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
import net.minecraft.network.chat.Component

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

abstract class UScreen(
    val restoreCurrentGuiOnClose: Boolean = false,
    open var newGuiScale: Int = -1,
    open var unlocalizedName: String? = null
) : Screen(Component.translatable(unlocalizedName ?: "")) {
    @JvmOverloads
    constructor(
        restoreCurrentGuiOnClose: Boolean = false,
        newGuiScale: Int = -1,
    ) : this(restoreCurrentGuiOnClose, newGuiScale, null)

    private var guiScaleToRestore = -1
    private var restoringGuiScale = false
    private val screenToRestore: Screen? = if (restoreCurrentGuiOnClose) Client.currentGui.get() else null
    private var suppressBackground = true

    private val advancedDrawContext = AdvancedDrawContext()

    //#if MC<=12111
    //$$private var drawContexts = mutableListOf<GuiGraphics>()
    //$$private inline fun <R> withDrawContext(matrixStack: UMatrixStack, block: (GuiGraphics) -> R) {
    //#else
    private var drawContexts = mutableListOf<GuiGraphicsExtractor>()
    private inline fun <R> withDrawContext(matrixStack: UMatrixStack, block: (GuiGraphicsExtractor) -> R) {
    //#endif
        val context = drawContexts.last()
        context.pose().pushMatrix()
        matrixStack.to3x2Joml(context.pose())
        block(context)
        context.pose().popMatrix()
    }

    private var lastClick = 0L
    private var lastDraggedDx = -1.0
    private var lastDraggedDy = -1.0
    private var lastScrolledX = -1.0
    private var lastScrolledY = -1.0
    private var lastScrolledDX = 0.0

    final override fun init() {
        updateGuiScale()
        initScreen(width, height)
    }

    override fun getTitle(): Component = Component.translatable(unlocalizedName ?: "")

    //#if MC<=12111
    //$$final override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    //#else
    final override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
    //#endif
        drawContexts.add(context)
        advancedDrawContext.nextFrame()
        advancedDrawContext.drawImmediate(context) { stack ->
            suppressBackground = false
            onDrawScreen(stack, mouseX, mouseY, delta)
            suppressBackground = true
        }
        drawContexts.removeLast()
    }

    final override fun keyPressed(input: KeyEvent): Boolean {
        onKeyPressed(input.key, 0.toChar(), input.modifiers.toModifiers())
        return false
    }

    final override fun keyReleased(input: KeyEvent): Boolean {
        onKeyReleased(input.key, 0.toChar(), input.modifiers.toModifiers())
        return false
    }

    final override fun charTyped(input: CharacterEvent): Boolean {
        val codepoint = input.codepoint
        //#if MC<=12111
        //$$val modifiers = input.modifiers.toModifiers()
        //#else
        val modifiers = 0.toModifiers()
        //#endif
        if (Character.isBmpCodePoint(codepoint)) {
            onKeyPressed(0, input.codepoint.toChar(), modifiers)
        } else if (Character.isValidCodePoint(codepoint)) {
            onKeyPressed(0, Character.highSurrogate(input.codepoint), modifiers)
            onKeyPressed(0, Character.lowSurrogate(input.codepoint), modifiers)
        }
        return false
    }

    private var lastMouseInput: MouseButtonInfo? = null
    private var lastDoubled: Boolean? = null

    final override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        lastMouseInput = click.buttonInfo
        lastDoubled = doubled
        if (click.button() == 1) lastClick = Client.getTime()
        onMouseClicked(click.x, click.y, click.button())
        lastMouseInput = null
        lastDoubled = null
        return false
    }

    final override fun mouseReleased(click: MouseButtonEvent): Boolean {
        lastMouseInput = click.buttonInfo
        onMouseReleased(click.x, click.y, click.button())
        lastMouseInput = null
        return false
    }

    override fun mouseDragged(click: MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean {
        lastMouseInput = click.buttonInfo
        lastDraggedDx = offsetX
        lastDraggedDy = offsetY
        onMouseDragged(click.x, click.y, click.button(), Client.getTime() - lastClick)
        lastMouseInput = null
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, delta: Double): Boolean {
        lastScrolledDX = horizontalAmount
        lastScrolledX = mouseX
        lastScrolledY = mouseY
        @Suppress("DEPRECATION")
        onMouseScrolled(delta)
        return false
    }

    final override fun tick(): Unit = onTick()

    final override fun onClose() {
        advancedDrawContext.close()
        onScreenClose()
        restoreGuiScale()
    }

    private var lastBackgroundMouseX = 0
    private var lastBackgroundMouseY = 0
    private var lastBackgroundDelta = 0f
    //#if MC<=12111
    //$$final override fun renderBackground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
    //#else
    final override fun extractBackground(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
    //#endif
        lastBackgroundMouseX = mouseX
        lastBackgroundMouseY = mouseY
        lastBackgroundDelta = delta
        if (suppressBackground) return
        drawContexts.add(context)
        drawContexts.removeLast()
    }

//    constructor(restoreCurrentGuiOnClose: Boolean, newGuiScale: GuiScale) : this(
//        restoreCurrentGuiOnClose,
//        newGuiScale.ordinal
//    )

    fun restorePreviousScreen() {
        Client.currentGui.set(screenToRestore)
    }

    open fun updateGuiScale() {
        if (newGuiScale != -1 && !restoringGuiScale) {
            if (guiScaleToRestore == -1) {
                guiScaleToRestore = Settings.video.getGuiScale()
            }
            Settings.video.setGuiScale(newGuiScale)
            width = Client.getMinecraft().window.width
            height = Client.getMinecraft().window.height
        }
    }

    private fun restoreGuiScale() {
        if (guiScaleToRestore != -1) {
            restoringGuiScale = true
            Settings.video.setGuiScale(guiScaleToRestore)
            restoringGuiScale = false
            guiScaleToRestore = -1
        }
    }

    open fun initScreen(width: Int, height: Int) {
        super.init()
    }

    open fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        withDrawContext(matrixStack) { drawContext ->
            //#if MC<=12111
            //$$super.render(drawContext, mouseX, mouseY, partialTicks)
            //#else
            super.extractRenderState(drawContext, mouseX, mouseY, partialTicks)
            //#endif
        }
    }

    open fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: KeyModifiers?) {
        if (keyCode != 0) {
            super.keyPressed(KeyEvent(keyCode, 0, modifiers.toInt()))
        }
        if (typedChar != 0.toChar()) {
            //#if MC<=12111
            //$$super.charTyped(CharacterEvent(typedChar.code, modifiers.toInt()))
            //#else
            super.charTyped(CharacterEvent(typedChar.code))
            //#endif
        }
    }

    open fun onKeyReleased(keyCode: Int, typedChar: Char, modifiers: KeyModifiers?) {
        if (keyCode != 0) {
            super.keyReleased(KeyEvent(keyCode, 0, modifiers.toInt()))
        }
    }

    open fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        if (mouseButton == 1) {
            lastClick = Client.getTime()
        }
        super.mouseClicked(MouseButtonEvent(mouseX, mouseY, MouseButtonInfo(mouseButton, lastMouseInput?.modifiers ?: 0)), lastDoubled ?: false)
    }

    open fun onMouseReleased(mouseX: Double, mouseY: Double, state: Int) {
        super.mouseReleased(MouseButtonEvent(mouseX, mouseY, MouseButtonInfo(state, lastMouseInput?.modifiers ?: 0)))
    }

    open fun onMouseDragged(x: Double, y: Double, clickedButton: Int, timeSinceLastClick: Long) {
        super.mouseDragged(MouseButtonEvent(x, y, MouseButtonInfo(clickedButton, lastMouseInput?.modifiers ?: 0)), lastDraggedDx, lastDraggedDy)
    }

    @Deprecated("Provided `delta` values have different units depending on Minecraft versions.", ReplaceWith("onMouseScrolled(mouseX, mouseY, deltaHorizontal, deltaVertical)"))
    open fun onMouseScrolled(delta: Double) {
        onMouseScrolled(lastScrolledX, lastScrolledY, lastScrolledDX, delta)
    }
    open fun onMouseScrolled(mouseX: Double, mouseY: Double, deltaHorizontal: Double, deltaVertical: Double) {
        super.mouseScrolled(mouseX, mouseY, deltaHorizontal, deltaVertical)
    }

    open fun onTick() {
        super.tick()
    }

    open fun onScreenClose() {
        super.onClose()
    }

    open fun onDrawBackground(matrixStack: UMatrixStack, tint: Int) {
        withDrawContext(matrixStack) { drawContext ->
            drawContext.nextStratum()
            val orgProjectionMatrixBuffer = RenderSystem.getProjectionMatrixBuffer()!!
            val orgProjectionType = RenderSystem.getProjectionType()
            //#if MC<=12111
            //$$super.renderBackground(drawContext, lastBackgroundMouseX, lastBackgroundMouseY, lastBackgroundDelta)
            //#else
            super.extractBackground(drawContext, lastBackgroundMouseX, lastBackgroundMouseY, lastBackgroundDelta)
            //#endif
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            RenderSystem.setProjectionMatrix(orgProjectionMatrixBuffer, orgProjectionType)
            drawContext.nextStratum()
        }
    }
}
