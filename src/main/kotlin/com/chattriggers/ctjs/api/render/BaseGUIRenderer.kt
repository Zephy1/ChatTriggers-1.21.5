package com.chattriggers.ctjs.api.render

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import com.chattriggers.ctjs.api.render.RenderUtils.RGBAColor
import com.chattriggers.ctjs.api.render.RenderUtils.RenderColor
import net.minecraft.client.renderer.texture.DynamicTexture

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

abstract class BaseGUIRenderer {
    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     */
    @JvmOverloads
    fun drawStringWithShadowRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(
            drawContext,
            text, xPosition, yPosition, RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset
        )
    }

    @JvmOverloads
    fun drawStringWithShadowRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, RGBAColor.fromIntArray(colorArray).getLongRGBA(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmOverloads
    fun drawStringWithShadowRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, color.getLongRGBA(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    /**
     * Draws text with a shadow to the screen
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     */
    @JvmOverloads
    fun drawStringWithShadow(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     */
    @JvmOverloads
    fun drawStringRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmOverloads
    fun drawStringRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, RGBAColor.fromIntArray(colorArray).getLongRGBA(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmOverloads
    fun drawStringRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(drawContext, text, xPosition, yPosition, color.getLongRGBA(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    /**
     * Draws text to the screen
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param textScale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param textShadow whether to draw a shadow behind the text
     */
    abstract fun drawString(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    )

    /**
     * Draws a line on the screen from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param lineThickness the thickness of the line
     */
    @JvmOverloads
    fun drawLineRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        drawLine(drawContext, startX, startY, endX, endY, RGBAColor(red, green, blue, alpha).getLong(), lineThickness, zOffset)
    }

    @JvmOverloads
    fun drawLineRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        drawLine(drawContext, startX, startY, endX, endY, RGBAColor.fromIntArray(colorArray).getLongRGBA(), lineThickness, zOffset)
    }

    @JvmOverloads
    fun drawLineRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        drawLine(drawContext, startX, startY, endX, endY, color.getLongRGBA(), lineThickness, zOffset)
    }

    /**
     * Draws a line on the screen from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param lineThickness the thickness of the line
     */
    @JvmOverloads
    fun drawLine(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        val dx = endX - startX
        val dy = endY - startY
        val len = sqrt(dx * dx + dy * dy)
        val halfThickness = lineThickness / 2f
        val offsetX = if (len > 0) -dy / len * halfThickness else 0f
        val offsetY = if (len > 0) dx / len * halfThickness else 0f

        val vertexList = listOf(
            Pair(startX + offsetX, startY + offsetY),
            Pair(endX + offsetX, endY + offsetY),
            Pair(endX - offsetX, endY - offsetY),
            Pair(startX - offsetX, startY - offsetY)
        )
        _drawLine(drawContext, vertexList, color, zOffset)
    }

    abstract fun _drawLine(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     */
    @JvmOverloads
    fun drawSquareRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmOverloads
    fun drawSquareRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, RGBAColor.fromIntArray(colorArray).getLongRGBA(), zOffset)
    }

    @JvmOverloads
    fun drawSquareRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, color.getLongRGBA(), zOffset)
    }

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param color the color as a [Long] value in RGBA format
     */
    @JvmOverloads
    fun drawSquare(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, color, zOffset)
    }

    /**
     * Draws a rectangle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     */
    @JvmOverloads
    fun drawRectRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, width, height, RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmOverloads
    fun drawRectRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, width, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), zOffset)
    }

    @JvmOverloads
    fun drawRectRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        zOffset: Float = 0f,
    ) {
        drawRect(drawContext, xPosition, yPosition, width, height, color.getLongRGBA(), zOffset)
    }

    /**
     * Draws a rectangle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color as a [Long] value in RGBA format
     */
    @JvmOverloads
    fun drawRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val x1 = xPosition
        val x2 = xPosition + width
        val y1 = yPosition
        val y2 = yPosition + height
        val vertexList = listOf(
            Pair(x1, y1),
            Pair(x1, y2),
            Pair(x2, y2),
            Pair(x2, y1),
        )
        _drawRect(drawContext, vertexList, color, zOffset)
    }

    abstract fun _drawRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawRoundedRectRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        radius: Float = 4f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        drawRoundedRect(drawContext, xPosition, yPosition, width, height, radius, RGBAColor(red, green, blue, alpha).getLong(), flatCorners, segments, zOffset)
    }

    @JvmOverloads
    fun drawRoundedRectRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        radius: Float = 4f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        drawRoundedRect(drawContext, xPosition, yPosition, width, height, radius, RGBAColor.fromIntArray(colorArray).getLongRGBA(), flatCorners, segments, zOffset)
    }

    @JvmOverloads
    fun drawRoundedRectRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        radius: Float = 4f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        drawRoundedRect(drawContext, xPosition, yPosition, width, height, radius, color.getLongRGBA(), flatCorners, segments, zOffset)
    }

    @JvmOverloads
    fun drawRoundedRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        radius: Float = 4f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        val x1 = xPosition
        val y1 = yPosition
        val x2 = xPosition + width
        val y2 = yPosition + height

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val clampedRadius = radius.coerceAtMost(minOf(width, height) / 2f)
        val flatCornersSet = flatCorners.toSet()

        val edgeVertices = mutableListOf<Pair<Float, Float>>()
        fun addCornerVertices(cx: Float, cy: Float, r: Float, startAngle: Float, endAngle: Float, segs: Int) {
            val angleStep = (endAngle - startAngle) / segs
            for (i in 1..segs) {
                val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                edgeVertices.add(Pair(x, y))
            }
        }

        edgeVertices.add(Pair(x2 - clampedRadius, y1))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y1))
        } else {
            addCornerVertices(x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            edgeVertices.add(Pair(x2, y1 + clampedRadius))
        }

        edgeVertices.add(Pair(x2, y2 - clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y2))
        } else {
            addCornerVertices(x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            edgeVertices.add(Pair(x2 - clampedRadius, y2))
        }

        edgeVertices.add(Pair(x1 + clampedRadius, y2))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y2))
        } else {
            addCornerVertices(x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            edgeVertices.add(Pair(x1, y2 - clampedRadius))
        }

        edgeVertices.add(Pair(x1, y1 + clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y1))
        } else {
            addCornerVertices(x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            edgeVertices.add(Pair(x1 + clampedRadius, y1))
        }
        edgeVertices.add(Pair(x2 - clampedRadius, y1))

        val vertexList = mutableListOf<Pair<Float, Float>>()
        for (i in 0 until edgeVertices.size - 1) {
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[i + 1])
            vertexList.add(edgeVertices[i])
        }

        _drawRoundedRect(drawContext, x1, y1, x2, y2, vertexList, color, zOffset)
    }

    abstract fun _drawRoundedRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawSimpleGradientRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startRed: Int = 255,
        startGreen: Int = 255,
        startBlue: Int = 255,
        startAlpha: Int = 255,
        endRed: Int = 0,
        endGreen: Int = 0,
        endBlue: Int = 0,
        endAlpha: Int = 255,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val startColor = RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(drawContext, x, y, width, height, startColor, endColor, direction, zOffset)
    }

    @JvmOverloads
    fun drawSimpleGradientRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startColorArray: IntArray = intArrayOf(255, 255, 255, 255),
        endColorArray: IntArray = intArrayOf(255, 255, 255, 255),
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val startColor = RGBAColor.fromIntArray(startColorArray).getLong()
        val endColor = RGBAColor.fromIntArray(endColorArray).getLong()
        drawSimpleGradient(drawContext, x, y, width, height, startColor, endColor, direction, zOffset)
    }

    @JvmOverloads
    fun drawSimpleGradient(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startColor: Long = RenderUtils.WHITE,
        endColor: Long = RenderUtils.BLACK,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val gradientColors = RenderUtils.getGradientColors(direction, startColor, endColor)
        drawGradient(drawContext, x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, direction, zOffset)
    }

    @JvmOverloads
    fun drawGradient(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeftColor: Long = RenderUtils.WHITE,
        topRightColor: Long = RenderUtils.WHITE,
        bottomLeftColor: Long = RenderUtils.BLACK,
        bottomRightColor: Long = RenderUtils.BLACK,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val x2 = x + width
        val y2 = y + height

        // This is really bad, but it works
        val vertexAndColorList = when (direction) {
            RenderUtils.GradientDirection.TOP_TO_BOTTOM,
            RenderUtils.GradientDirection.RIGHT_TO_LEFT,
            RenderUtils.GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x2, y, topRightColor),
                )
            }

            RenderUtils.GradientDirection.BOTTOM_TO_TOP,
            RenderUtils.GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> {
                listOf(
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                )
            }

            RenderUtils.GradientDirection.LEFT_TO_RIGHT,
            RenderUtils.GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                )
            }

            RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> {
                listOf(
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                )
            }
        }

        _drawGradient(drawContext, vertexAndColorList, zOffset)
    }

    abstract fun _drawGradient(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexAndColorList: List<Triple<Float, Float, Long>>,
        zOffset: Float = 0f,
    )

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param radius the radius of the circle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmOverloads
    fun drawSimpleCircleRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f, zOffset)
    }

    @JvmOverloads
    fun drawSimpleCircleRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, RGBAColor.fromIntArray(colorArray).getLongRGBA(), edges, 0f, 0f, 0f, zOffset)
    }

    @JvmOverloads
    fun drawSimpleCircleRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, color.getLongRGBA(), edges, 0f, 0f, 0f, zOffset)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param radius the radius of the circle
     * @param color the color as a [Long] value in RGBA format
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmOverloads
    fun drawSimpleCircle(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f, zOffset)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param xScale the X-radius of the circle
     * @param yScale the Y-radius of the circle
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmOverloads
    fun drawCircleRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, xScale, yScale, RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset)
    }

    @JvmOverloads
    fun drawCircleRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, xScale, yScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset)
    }

    @JvmOverloads
    fun drawCircleRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, xScale, yScale, color.getLongRGBA(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset)
    }

    /**
     * Draws a circle to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param xScale the X-radius of the circle
     * @param yScale the Y-radius of the circle
     * @param color the color as a [Long] value in RGBA format
     * @param edges the number of edges
     * @param rotationDegrees number of degrees to rotate the circle on the Z-axis
     * @param xRotationOffset the X-offset for the rotation
     * @param yRotationOffset the Y-offset for the rotation
     */
    @JvmOverloads
    fun drawCircle(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        val angleStep = 2f * PI.toFloat() / edges
        val rotationRadians = rotationDegrees * PI.toFloat() / 180f
        val cos = cos(rotationRadians)
        val sin = sin(rotationRadians)

        val centerX = xPosition + xRotationOffset
        val centerY = yPosition + yRotationOffset

        val vertexList = mutableListOf<Pair<Float, Float>>()

        val edgeVertices = Array(edges) { i ->
            val angle = i * angleStep
            val baseX = cos(angle) * xScale
            val baseY = sin(angle) * yScale
            val relX = baseX - xRotationOffset
            val relY = baseY - yRotationOffset
            val x = relX * cos - relY * sin + xRotationOffset + xPosition
            val y = relX * sin + relY * cos + yRotationOffset + yPosition
            Pair(x, y)
        }

        for (i in 0 until edges) {
            val nextIndex = (i + 1) % edges
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[nextIndex])
            vertexList.add(edgeVertices[i])
        }

        val minX = xPosition - xScale
        val maxX = xPosition + xScale
        val minY = yPosition - yScale
        val maxY = yPosition + yScale
        _drawCircle(
            drawContext,
            minX, maxX,
            minY, maxY,
            vertexList, color, zOffset
        )
    }

    abstract fun _drawCircle(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawImageRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawImage(drawContext, image, xPosition, yPosition, width, height, RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmOverloads
    fun drawImageRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        zOffset: Float = 0f,
    ) {
        drawImage(drawContext, image, xPosition, yPosition, width, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), zOffset)
    }

    @JvmOverloads
    fun drawImageRenderColor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        zOffset: Float = 0f,
    ) {
        drawImage(drawContext, image, xPosition, yPosition, width, height, color.getLongRGBA(), zOffset)
    }

    /**
     * Draws an image to the screen
     *
     * @param image the image
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param width new image width
     * @param height new image height
     */
    @JvmOverloads
    fun drawImage(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val texture = image.getTexture() ?: throw IllegalStateException("Image is null.")
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        val vertexList = listOf(
            Pair(xPosition, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition),
            Pair(xPosition, yPosition)
        )
        val uvList = listOf(
            Pair(0f, 1f),
            Pair(1f, 1f),
            Pair(1f, 0f),
            Pair(0f, 0f),
        )
        _drawImage(drawContext, image, texture, vertexList, uvList, color, zOffset)
    }

    abstract fun _drawImage(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        texture: DynamicTexture,
        vertexList: List<Pair<Float, Float>>,
        uvList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    )
}
