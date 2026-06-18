package com.chattriggers.ctjs.api.render.renderstates

import com.chattriggers.ctjs.api.render.RenderUtils
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import org.joml.Matrix3x2f

//#if MC<=12111
//$$import net.minecraft.client.gui.render.state.GuiElementRenderState
//#else
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
//#endif

class GUIRenderState(
    val matrix: Matrix3x2f,
    val vertexList: List<Pair<Float, Float>>,
    val boundsList: List<Pair<Float, Float>>,
    val zOffset: Float,
    val color: RenderUtils.RenderColor,
    val pipeline: RenderPipeline,
    val scissorArea: ScreenRectangle?,
) : GuiElementRenderState {
    override fun buildVertices(vertices: VertexConsumer) {
        val zPosition = zOffset
        val newMatrix = RenderUtils.getGUIMatrix(matrix)
        val (r, g, b, a) = color.getIntComponentsRGBA()
        vertexList.forEach { (x, y) ->
            vertices
                .addVertex(newMatrix, x, y, zPosition)
                .setColor(r, g, b, a)
        }
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = scissorArea
    override fun bounds(): ScreenRectangle? {
        if (boundsList.isEmpty()) return null

        val (minX, minY) = boundsList[0]
        val (maxX, maxY) = boundsList[2]

        val rect = ScreenRectangle(
            minX.toInt(),
            minY.toInt(),
            (maxX - minX).toInt(),
            (maxY - minY + 1).toInt(),
        ).transformMaxBounds(matrix)

        return scissorArea?.intersection(rect) ?: rect
    }
}
