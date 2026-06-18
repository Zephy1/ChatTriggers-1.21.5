package com.chattriggers.ctjs.api.render.renderstates

import com.chattriggers.ctjs.api.render.RenderUtils
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup

//#if MC<=12111
//$$import net.minecraft.client.gui.render.state.GuiElementRenderState
//#else
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
//#endif

class GradientGUIRenderState(
    private val base: GUIRenderState,
    val vertexAndColorList: List<Triple<Float, Float, Long>>,
) : GuiElementRenderState {
    override fun buildVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        vertexAndColorList.forEach { (x, y, color) ->
            val (r, g, b, a) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
            vertices
                .addVertex(newMatrix, x, y, zPosition)
                .setColor(r, g, b, a)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = base.scissorArea
    override fun bounds(): ScreenRectangle? = base.bounds()
}
