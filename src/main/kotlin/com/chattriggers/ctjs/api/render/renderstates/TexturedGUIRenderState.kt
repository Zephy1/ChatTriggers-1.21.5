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

class TexturedGUIRenderState(
    private val base: GUIRenderState,
    val textureSetup: TextureSetup,
    val uvList: List<Pair<Float, Float>>,
) : GuiElementRenderState {
    override fun buildVertices(vertices: VertexConsumer) {
        val zPosition = base.zOffset
        val newMatrix = RenderUtils.getGUIMatrix(base.matrix)
        val (r, g, b, a) = base.color.getIntComponentsRGBA()

        base.vertexList.forEachIndexed { index, (x, y) ->
            val (u, v) = uvList.getOrNull(index) ?: Pair(0f, 0f)
            vertices
                .addVertex(newMatrix, x, y, zPosition)
                .setColor(r, g, b, a)
                .setUv(u, v)
        }
    }

    override fun pipeline(): RenderPipeline = base.pipeline
    override fun textureSetup(): TextureSetup = textureSetup
    override fun scissorArea(): ScreenRectangle? = base.scissorArea
    override fun bounds(): ScreenRectangle? = base.bounds()
}
