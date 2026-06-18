package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.client.CTPlayer
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.render.RenderUtils
import com.chattriggers.ctjs.api.render.Text
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

object ModulesGui : Screen(net.minecraft.network.chat.Component.literal("Modules")) {
    private val window = object {
        val title = Text("Modules").setScale(2f).setShadow(true)
        val exit = Text(ChatLib.addColor("&cx")).setScale(2f)
        var height = 0f
        var scroll = 0f
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
        mouseX: Int, mouseY: Int, deltaTicks: Float) {
        drawContext.pose().pushMatrix()

        drawContext.fill(0, 0, drawContext.guiWidth(), drawContext.guiHeight(), 0x50000000)
        val middle = RenderUtils.screen.getWidth() / 2
        val width = (RenderUtils.screen.getWidth() - 100).coerceAtMost(500)

        GUIRenderer.drawRect(
            drawContext,
            0f,
            0f,
            RenderUtils.screen.getWidth().toFloat(),
            RenderUtils.screen.getHeight().toFloat(),
            0x50000000,
        )

        if (-window.scroll > window.height - RenderUtils.screen.getHeight() + 20)
            window.scroll = -window.height + RenderUtils.screen.getHeight() - 20
        if (-window.scroll < 0) window.scroll = 0f

        if (-window.scroll > 0) {
            val width = RenderUtils.screen.getWidth()
            val height = RenderUtils.screen.getHeight()
            GUIRenderer.drawRect(drawContext, width - 20f, height - 20f, 20f, 20f, 0xAA000000)
            GUIRenderer.drawString(drawContext, "^", width - 12f, height - 12f)
        }

        val ox = middle - width / 2
        val oy = window.scroll.toInt() + 95

        drawContext.fill(ox, oy, ox + width, oy + (window.height.toInt() - 90), 0x50000000)
        drawContext.fill(ox, oy, ox + width, oy + 25, 0xaa000000.toInt())

        window.title.draw(drawContext, (middle - width / 2 + 5) / 2, (window.scroll.toInt() + 100) / 2)
        window.exit.draw(drawContext, (middle + width / 2 - 17) / 2, (window.scroll.toInt() + 99) / 2)

        window.height = 125f
        ModuleManager.cachedModules.sortedBy { it.name }.forEach {
            window.height += it.draw(drawContext, middle - width / 2, (window.scroll + window.height).toInt(), width)
        }

        drawContext.pose().popMatrix()
    }

    override fun mouseClicked(click: MouseButtonEvent, double: Boolean): Boolean {
        super.mouseClicked(click, double)
        val mouseX = click.x
        val mouseY = click.y
        var width = RenderUtils.screen.getWidth() - 100f
        if (width > 500) width = 500f

        if (mouseX > RenderUtils.screen.getWidth() - 20 && mouseY > RenderUtils.screen.getHeight() - 20) {
            window.scroll = 0f
            return false
        }

        if (mouseX > RenderUtils.screen.getWidth() / 2f + width / 2f - 25 &&
            mouseX < RenderUtils.screen.getWidth() / 2f + width / 2f &&
            mouseY > window.scroll + 95 &&
            mouseY < window.scroll + 120
        ) {
            CTPlayer.toMC()?.clientSideCloseContainer()
            return false
        }

        ModuleManager.cachedModules.toList().forEach {
            it.click(mouseX, mouseY, width)
        }

        return false
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        delta: Double
    ): Boolean {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, delta)
        window.scroll += delta.toFloat()
        return false
    }
}
