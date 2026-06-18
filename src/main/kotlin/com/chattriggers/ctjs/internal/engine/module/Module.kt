package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.RenderUtils
import com.chattriggers.ctjs.api.render.Text
import com.fasterxml.jackson.core.Version
import java.io.File

//#if MC<=12111
//$$import org.joml.Quaternionf
//#endif

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

class Module(val name: String, var metadata: ModuleMetadata, val folder: File) {
    var targetModVersion: Version? = null
    var requiredBy = mutableSetOf<String>()

    private val gui = object {
        var collapsed = true
        var x = 0
        var y = 0
        var description = Text(metadata.description ?: "No description provided in the metadata")
    }

    fun draw(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Int,
        y: Int,
        width: Int
    ): Int {
        gui.x = x
        gui.y = y

        drawContext.pose().pushMatrix()

        drawContext.fill(x, y, x + width, y + 13, 0xaa000000.toInt())
        //#if MC<=12111
        //$$drawContext.drawString(
        //#else
        drawContext.text(
        //#endif
            RenderUtils.getTextRenderer(),
            metadata.name ?: name,
            x + 3, y + 3, -1
        )

        return if (gui.collapsed) {
            drawContext.pose().pushMatrix()
            drawContext.pose().translate(x + width - 5f, y + 8f)
            drawContext.pose().rotate(Math.PI.toFloat())
            //#if MC<=12111
            //$$drawContext.drawString(
            //#else
            drawContext.text(
            //#endif
                RenderUtils.getTextRenderer(), "^", 0, 0, -1, false
            )
            drawContext.pose().popMatrix()
            16
        } else {
            gui.description.setMaxWidth(width - 5)

            drawContext.fill(x, y + 13, x + width, y + (gui.description.getHeight() + 25), 0x50000000)
            //#if MC<=12111
            //$$drawContext.drawString(
            //#else
            drawContext.text(
            //#endif
                RenderUtils.getTextRenderer(), "^", x + width - 10, y + 5, -1, false
            )

            gui.description.draw(drawContext, x + 3, y + 15)

            if (metadata.version != null) {
            //#if MC<=12111
            //$$drawContext.drawString(
            //#else
            drawContext.text(
            //#endif
                    RenderUtils.getTextRenderer(),
                    ChatLib.addColor("&8v${metadata.version}"),
                    x + width - RenderUtils.getStringWidth(ChatLib.addColor("&8v${metadata.version}")),
                    y + gui.description.getHeight() + 15,
                    -1
                )
            }

            //#if MC<=12111
            //$$drawContext.drawString(
            //#else
            drawContext.text(
            //#endif
                RenderUtils.getTextRenderer(),
                ChatLib.addColor(
                    if (metadata.isRequired && requiredBy.isNotEmpty()) {
                        "&8required by $requiredBy"
                    } else {
                        "&4[delete]"
                    },
                ),
                x + 3,
                y + gui.description.getHeight() + 15,
                -1
            )

            drawContext.pose().popMatrix()
            gui.description.getHeight() + 27
        }
    }

    fun click(x: Double, y: Double, width: Float) {
        if (x > gui.x &&
            x < gui.x + width &&
            y > gui.y &&
            y < gui.y + 13
        ) {
            gui.collapsed = !gui.collapsed
            return
        }

        if (gui.collapsed || (metadata.isRequired && requiredBy.isNotEmpty())) return

        if (x > gui.x &&
            x < gui.x + 45 &&
            y > gui.y + gui.description.getHeight() + 15 &&
            y < gui.y + gui.description.getHeight() + 25
        ) {
            ModuleManager.deleteModule(name)
        }
    }

    override fun toString() = "Module{name=$name,version=${metadata.version}}"
}
