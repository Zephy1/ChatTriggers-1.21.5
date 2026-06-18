package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.font.TextRenderable
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.Component
import org.joml.Matrix4f
import java.awt.Color
import com.chattriggers.ctjs.internal.listeners.WorldListener
import com.mojang.blaze3d.systems.RenderSystem
import org.joml.Quaternionf
//#if MC>=26.2
import net.minecraft.client.renderer.StagedVertexBuffer
import net.minecraft.client.renderer.rendertype.PreparedRenderType
//#endif

object WorldRenderer : BaseWorldRenderer() {
    override fun drawString(text: String, xPosition: Float, yPosition: Float, zPosition: Float, color: Long, scale: Float, renderBackground: Boolean, centered: Boolean, textShadow: Boolean, disableDepth: Boolean, maxWidth: Int) {
        drawText(Component.literal(text), xPosition, yPosition, zPosition, color, scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmStatic
    fun getLineRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.LINES_ESP() else RenderLayers.LINES()

    @JvmStatic
    fun getQuadRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.QUADS_ESP() else RenderLayers.QUADS()

    @JvmStatic
    fun getTriangleStripRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLE_STRIP_ESP() else RenderLayers.TRIANGLE_STRIP()

    @JvmStatic
    fun getTriangleRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLES_ESP() else RenderLayers.TRIANGLES()

    @JvmStatic
    @JvmOverloads
    fun drawTextRGBA(text: Component, xPosition: Float, yPosition: Float, zPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, scale: Float = 1f, renderBackground: Boolean = false, centered: Boolean = false, textShadow: Boolean = true, disableDepth: Boolean = false, maxWidth: Int = 512) {
        drawText(text, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmStatic
    @JvmOverloads
    fun drawText(
        text: Component,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        val (lines, width, height) = RenderUtils.splitText(text, maxWidth)
        val fontRenderer = RenderUtils.getTextRenderer()
        val camera = RenderUtils.getCamera()
        val cameraPos = RenderUtils.getCameraPos()
        //#if MC<26.2
        //$$val vertexConsumers = Client.getMinecraft().renderBuffers().bufferSource()
        //#endif

        val adjustedScale = (scale * 0.05).toFloat()
        val xShift = -width / 2
        val yShift = -height / 2
        var yOffset = 0
        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            Color(0, 0, 0, 0).rgb
        }

        //#if MC>=26.2
        val displayMode = if (disableDepth) Font.DisplayMode.SEE_THROUGH else Font.DisplayMode.NORMAL
        val stagedVertexBuffer = Client.getMinecraft().gameRenderer.renderBuffers().stagedVertexBuffer()
        val draws = mutableMapOf<RenderType, Pair<StagedVertexBuffer.Draw, PreparedRenderType>>()
        //#endif

        RenderUtils.baseStartDraw()
        for (line in lines) {
            //#if MC<26.2
            //$$val matrix = Matrix4f()
            //#else
            val matrix = Matrix4f(WorldListener.viewMatrix)
            //#endif
                .translate(
                    (xPosition - cameraPos.x).toFloat(),
                    (yPosition - cameraPos.y + yOffset * adjustedScale).toFloat(),
                    (zPosition - cameraPos.z).toFloat(),
                )
                .rotate(camera.rotation())
                .scale(adjustedScale, -adjustedScale, adjustedScale)

            val centerShift = if (centered) {
                xShift + (fontRenderer.width(line) / 2f)
            } else {
                0f
            }

            //#if MC<26.2
            //$$fontRenderer.drawInBatch(
            //$$    line,
            //$$    xShift - centerShift,
            //$$    yShift + yOffset,
            //$$    RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
            //$$    textShadow,
            //$$    matrix,
            //$$    vertexConsumers,
            //$$    if (disableDepth) Font.DisplayMode.SEE_THROUGH else Font.DisplayMode.NORMAL,
            //$$    backgroundColorInt,
            //$$    15728880, // FULL_BRIGHT
            //$$)
            //#else
            val prepared = fontRenderer.prepareText(
                line.visualOrderText,
                xShift - centerShift,
                yShift + yOffset,
                RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
                textShadow,
                false,
                backgroundColorInt,
            )
            prepared.visit(object : Font.GlyphVisitor {
                override fun acceptRenderable(renderable: TextRenderable) {
                    val renderType = renderable.renderType(displayMode)
                    val (draw, _) = draws.getOrPut(renderType) {
                        val draw = stagedVertexBuffer.appendDraw(renderType.format(), renderType.primitiveTopology())
                        draw to renderType.prepare()
                    }
                    renderable.render(matrix, stagedVertexBuffer.getVertexBuilder(draw), 15728880, false)
                }
            })
            //#endif

            yOffset += fontRenderer.lineHeight + 1
        }

        //#if MC<26.2
        //$$vertexConsumers.endBatch()
        //#else
        stagedVertexBuffer.upload()
        for ((draw, preparedRenderType) in draws.values) {
            val executeInfo = stagedVertexBuffer.getExecuteInfo(draw) ?: continue
            preparedRenderType.drawFromBuffer(executeInfo)
        }
        stagedVertexBuffer.endDraw()
        //#endif

        RenderUtils.worldEndDraw()
    }

    override fun _drawLine(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
    ) {
        val renderLayer = getLineRenderLayer(disableDepth)

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .begin(renderLayer)
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector, lineWidth) ->
            RenderUtils
                .pos(x, y, z)
                .lineWidth(lineWidth)
                .normal(normalVector)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawBox(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    ) {
        val renderLayer = when {
            !wireframe -> getTriangleStripRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .begin(renderLayer)
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector, lineWidth) ->
            RenderUtils
                .pos(x, y, z)
                .lineWidth(lineWidth)
                .normal(normalVector)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
    override fun _drawSphere(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    ) {
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .begin(renderLayer)
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector, lineWidth) ->
            RenderUtils
                .pos(x, y, z)
                .lineWidth(lineWidth)
                .normal(normalVector)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
    override fun _drawCylinder(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    ) {
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .begin(renderLayer)
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector, lineWidth) ->
            RenderUtils
                .pos(x, y, z)
                .lineWidth(lineWidth)
                .normal(normalVector)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawPyramid(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    ) {
        val renderLayer = when {
            !wireframe -> getTriangleRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        RenderUtils.baseStartDraw()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .begin(renderLayer)
            .colorizeRGBA(color)
        vertexAndNormalList.forEach { (x, y, z, normalVector, lineWidth) ->
            RenderUtils
                .pos(x, y, z)
                .lineWidth(lineWidth)
                .normal(normalVector)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun _drawTracer(
        partialTicks: Float,
        startPosX: Float,
        startPosY: Float,
        startPosZ: Float,
        endPosX: Float,
        endPosY: Float,
        endPosZ: Float,
        color: Long,
        disableDepth: Boolean,
        lineThickness: Float,
    ) {
        drawLine(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, color, disableDepth, lineThickness)
    }
}
