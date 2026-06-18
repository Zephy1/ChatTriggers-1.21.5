package com.chattriggers.ctjs.api.render

import org.joml.Vector3f
import com.chattriggers.ctjs.api.render.RenderUtils.setAndNormalize
import com.chattriggers.ctjs.api.render.RenderUtils.tempNormal
import com.chattriggers.ctjs.api.render.RenderUtils.RGBAColor
import com.chattriggers.ctjs.api.render.RenderUtils.RenderColor
import com.chattriggers.ctjs.internal.utils.get
import org.mozilla.javascript.NativeObject

abstract class BaseWorldRenderer {
    /**
     * Renders floating lines of text in the world
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     */
    @JvmOverloads
    fun drawStringRGBA(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, zPosition, RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmOverloads
    fun drawStringRGBAArray(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, zPosition, RGBAColor.fromIntArray(colorArray).getLongRGBA(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmOverloads
    fun drawStringRenderColor(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, zPosition, color.getLongRGBA(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    /**
     * A variant of drawString that takes an object instead of positional parameters
     */
    fun drawString(obj: NativeObject) {
        WorldRenderer.drawString(
            obj.get<String>("text") ?: error("Expected \"text\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("xPosition")?.toFloat()
                ?: error("Expected \"xPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("yPosition")?.toFloat()
                ?: error("Expected \"yPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("zPosition")?.toFloat()
                ?: error("Expected \"zPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("color")?.toLong() ?: RenderUtils.colorized ?: RenderUtils.WHITE,
            obj.get<Number>("scale")?.toFloat() ?: 1f,
            obj.get<Boolean>("renderBackground") ?: true,
            obj.get<Boolean>("disableDepth") ?: false,
            obj.get<Boolean>("textShadow") ?: true,
            obj.get<Boolean>("disableDepth") ?: true,
        )
    }

    /**
     * Renders floating lines of text in the world
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     */
    abstract fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512
    )

    private fun getLinePositions(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        lineThickness: Float = 1f,
    ): List<RenderUtils.WorldPositionVertex> {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val vectorCopy = Vector3f(tempNormal.setAndNormalize(endX - startX, endY - startY, endZ - startZ))

        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(startX, startY, startZ, vectorCopy, lineThickness))
        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(endX, endY, endZ, vectorCopy, lineThickness))

        return vertexAndNormalList
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the line through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawLineRGBA(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, startZ, endX, endY, endZ, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

    @JvmOverloads
    fun drawLineRGBAArray(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, startZ, endX, endY, endZ, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, lineThickness)
    }

    @JvmOverloads
    fun drawLineRenderColor(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, startZ, endX, endY, endZ, color.getLongRGBA(), disableDepth, lineThickness)
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to disable depth testing
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawLine(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val vertexAndNormalList = getLinePositions(startX, startY, startZ, endX, endY, endZ, lineThickness)
        _drawLine(vertexAndNormalList, color, disableDepth)
    }

    abstract fun _drawLine(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
    )

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleWireframeCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeCubeRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeCubeRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color.getLongRGBA(), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleWireframeCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawWireframeBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeBoxRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeBoxRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color.getLongRGBA(), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawWireframeBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleSolidCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    @JvmOverloads
    fun drawSimpleSolidCubeRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe = false)
    }

    @JvmOverloads
    fun drawSimpleSolidCubeRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color.getLongRGBA(), disableDepth, wireframe = false)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleSolidCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = false)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSolidBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    @JvmOverloads
    fun drawSolidBoxRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe = false)
    }

    @JvmOverloads
    fun drawSolidBoxRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color.getLongRGBA(), disableDepth, wireframe = false)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSolidBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = false)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawBoxRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawBoxRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color.getLongRGBA(), disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = xPosition - hw
        val x1 = xPosition + hw
        val y0 = yPosition - hh
        val y1 = yPosition + hh
        val z0 = zPosition - hd
        val z1 = zPosition + hd

        val vertexes = when {
            wireframe -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x1, y0, z0),
            )
            else -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
            )
        }

        for (i in 0 until vertexes.size) {
            val p1 = vertexes[i]
            if (wireframe) {
                val p2 = vertexes[(i + 1) % vertexes.size]
                val vectorCopy = Vector3f(tempNormal.setAndNormalize(p1, p2))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p2.x, p2.y, p2.z, vectorCopy, lineThickness))
            } else {
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, null, lineThickness))
            }
        }

        _drawBox(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawBox(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidSphereRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidSphereRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color.getLongRGBA(), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidSphereRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidSphereRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeSphereRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeSphereRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color.getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeSphereRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeSphereRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawSphereRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawSphereRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val cache = RenderUtils.getTrigCache(segments)

        if (wireframe) {
            for (lat in 1 until segments) {
                val sinPhi = cache.sinPhi[lat]
                val cosPhi = cache.cosPhi[lat]
                val y = yPosition + yScale * cosPhi
                for (lon in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[lon]
                    val sinTheta1 = cache.sinTheta[lon]
                    val cosTheta2 = cache.cosTheta[lon + 1]
                    val sinTheta2 = cache.sinTheta[lon + 1]

                    val x1 = xPosition + xScale * sinPhi * cosTheta1
                    val z1 = zPosition + zScale * sinPhi * sinTheta1

                    val x2 = xPosition + xScale * sinPhi * cosTheta2
                    val z2 = zPosition + zScale * sinPhi * sinTheta2

                    val vectorCopy = Vector3f(tempNormal.setAndNormalize(x2 - x1, 0f, z2 - z1))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y, z1, vectorCopy, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y, z2, vectorCopy, lineThickness))
                }
            }

            for (lon in 0 until (segments * 2)) {
                val cosTheta = cache.cosTheta[lon]
                val sinTheta = cache.sinTheta[lon]

                for (lat in 0 until segments) {
                    val sinPhi1 = cache.sinPhi[lat]
                    val cosPhi1 = cache.cosPhi[lat]
                    val sinPhi2 = cache.sinPhi[lat + 1]
                    val cosPhi2 = cache.cosPhi[lat + 1]

                    val x1 = xPosition + xScale * sinPhi1 * cosTheta
                    val y1 = yPosition + yScale * cosPhi1
                    val z1 = zPosition + zScale * sinPhi1 * sinTheta

                    val x2 = xPosition + xScale * sinPhi2 * cosTheta
                    val y2 = yPosition + yScale * cosPhi2
                    val z2 = zPosition + zScale * sinPhi2 * sinTheta

                    val vectorCopy = Vector3f(tempNormal.setAndNormalize(x2 - x1, y2 - y1, z2 - z1))

                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y1, z1, vectorCopy, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y2, z2, vectorCopy, lineThickness))
                }
            }
        } else {
            for (phi in 0 until segments) {
                val sinPhi1 = cache.sinPhi[phi]
                val cosPhi1 = cache.cosPhi[phi]
                val sinPhi2 = cache.sinPhi[phi + 1]
                val cosPhi2 = cache.cosPhi[phi + 1]

                for (theta in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[theta]
                    val sinTheta1 = cache.sinTheta[theta]
                    val cosTheta2 = cache.cosTheta[theta + 1]
                    val sinTheta2 = cache.sinTheta[theta + 1]

                    val x1 = xPosition + xScale * sinPhi1 * cosTheta1
                    val y1 = yPosition + yScale * cosPhi1
                    val z1 = zPosition + zScale * sinPhi1 * sinTheta1

                    val x2 = xPosition + xScale * sinPhi2 * cosTheta1
                    val y2 = yPosition + yScale * cosPhi2
                    val z2 = zPosition + zScale * sinPhi2 * sinTheta1

                    val x3 = xPosition + xScale * sinPhi2 * cosTheta2
                    val y3 = yPosition + yScale * cosPhi2
                    val z3 = zPosition + zScale * sinPhi2 * sinTheta2

                    val x4 = xPosition + xScale * sinPhi1 * cosTheta2
                    val y4 = yPosition + yScale * cosPhi1
                    val z4 = zPosition + zScale * sinPhi1 * sinTheta2

                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y1, z1, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y2, z2, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x3, y3, z3, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x4, y4, z4, null, lineThickness))
                }
            }
        }

        _drawSphere(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawSphere(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmOverloads
    fun drawSolidConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidConeRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidConeRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color.getLongRGBA(), segments, disableDepth, false)
    }

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmOverloads
    fun drawSolidCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeConeRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeConeRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color.getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawConeRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawConeRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color.getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color.getLongRGBA(), segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmOverloads
    fun drawSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color.getLongRGBA(), segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmOverloads
    fun drawSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframeCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color.getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframeCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color.getLongRGBA(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color.getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawSimpleCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawCylinderRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RGBAColor.fromIntArray(colorArray).getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawCylinderRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color.getLongRGBA(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()

        val bottomX = FloatArray(segments + 1)
        val bottomY = yPosition
        val bottomZ = FloatArray(segments + 1)
        val topX = FloatArray(segments + 1)
        val topY = bottomY + height
        val topZ = FloatArray(segments + 1)

        val cache = RenderUtils.getTrigCache(segments)
        for (i in 0..segments) {
            val thetaIndex = (i * 2) % (segments * 2)
            val cosA = cache.cosTheta[thetaIndex]
            val sinA = cache.sinTheta[thetaIndex]

            bottomX[i] = xPosition + bottomRadius * cosA
            bottomZ[i] = zPosition + bottomRadius * sinA
            topX[i] = xPosition + topRadius * cosA
            topZ[i] = zPosition + topRadius * sinA
        }

        if (wireframe) {
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                var vectorCopy = Vector3f(tempNormal.setAndNormalize(topX[next] - topX[i], 0f, topZ[next] - topZ[i]))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], vectorCopy, lineThickness))

                vectorCopy = Vector3f(tempNormal.setAndNormalize(bottomX[next] - bottomX[i], 0f, bottomZ[next] - bottomZ[i]))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], vectorCopy, lineThickness))

                vectorCopy = Vector3f(tempNormal.setAndNormalize(topX[i] - bottomX[i], topY - bottomY, topZ[i] - bottomZ[i]))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
            }
        } else {
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], null, lineThickness))
            }
        }

        if (bottomRadius > 0f) {
            val vectorCopy = Vector3f(0f, -1f, 0f)
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, bottomY, zPosition, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, bottomY, zPosition, vectorCopy, lineThickness))
            }
        }


        if (topRadius > 0f) {
            val vectorCopy = Vector3f(0f, 1f, 0f)
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, topY, zPosition, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, topY, zPosition, vectorCopy, lineThickness))
            }
        }

        _drawCylinder(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawCylinder(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidPyramidRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, false)
    }

    @JvmOverloads
    fun drawSimpleSolidPyramidRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color.getLongRGBA(), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSimpleSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidPyramidRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, false)
    }

    @JvmOverloads
    fun drawSolidPyramidRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmOverloads
    fun drawSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, false)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframePyramidRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawSimpleWireframePyramidRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color.getLongRGBA(), disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the radius of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawSimpleWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframePyramidRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, true, lineThickness)
    }

    @JvmOverloads
    fun drawWireframePyramidRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmOverloads
    fun drawWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, true, lineThickness)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawPyramidRGBAArray(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, wireframe, lineThickness)
    }

    @JvmOverloads
    fun drawPyramidRenderColor(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color.getLongRGBA(), disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the pyramid through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmOverloads
    fun drawPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val halfX = xScale / 2f
        val halfZ = zScale / 2f

        val x0 = xPosition - halfX
        val x1 = xPosition + halfX
        val y0 = yPosition
        val y1 = yPosition + yScale
        val z0 = zPosition - halfZ
        val z1 = zPosition + halfZ

        val apex = Vector3f(xPosition, y1, zPosition)
        val base00 = Vector3f(x0, y0, z0)
        val base10 = Vector3f(x1, y0, z0)
        val base11 = Vector3f(x1, y0, z1)
        val base01 = Vector3f(x0, y0, z1)

        val vertexes = if (wireframe) {
            listOf(
                base00, base10, base11, base01, base00,
                base00, apex,
                base10, apex,
                base11, apex,
                base01, apex,
            )
        } else {
            listOf(
                base00, base10, base11,
                base00, base11, base01,

                apex, base00, base10,
                apex, base10, base11,
                apex, base11, base01,
                apex, base01, base00,
            )
        }

        for (i in 0 until vertexes.size - if (wireframe) 1 else 0) {
            val p1 = vertexes[i]
            if (wireframe) {
                val p2 = vertexes[i + 1]
                val vectorCopy = Vector3f(tempNormal.setAndNormalize(p1, p2))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p2.x, p2.y, p2.z, vectorCopy, lineThickness))
            } else {
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, null, lineThickness))
            }
        }

        _drawPyramid(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawPyramid(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

    @JvmOverloads
    fun drawTracerRGBA(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawTracer(partialTicks, xPosition, yPosition, zPosition, RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

    @JvmOverloads
    fun drawTracerRGBAArray(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        colorArray: IntArray = intArrayOf(255, 255, 255, 255),
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawTracer(partialTicks, xPosition, yPosition, zPosition, RGBAColor.fromIntArray(colorArray).getLongRGBA(), disableDepth, lineThickness)
    }

    @JvmOverloads
    fun drawTracerRenderColor(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: RenderColor = RenderUtils.defaultRGBAColor,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawTracer(partialTicks, xPosition, yPosition, zPosition, color.getLongRGBA(), disableDepth, lineThickness)
    }

    @JvmOverloads
    fun drawTracer(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val camera = RenderUtils.getCamera()
        val lookVec = camera.forwardVector()
        val cameraPos = RenderUtils.getCameraPos(camera)
        val startPos = cameraPos.add(
            lookVec.x().toDouble(),
            lookVec.y().toDouble(),
            lookVec.z().toDouble(),
        )
        _drawTracer(partialTicks, startPos.x.toFloat(), startPos.y.toFloat(), startPos.z.toFloat(), xPosition, yPosition, zPosition, color, disableDepth, lineThickness)
    }

    abstract fun _drawTracer(
        partialTicks: Float,
        startPosX: Float,
        startPosY: Float,
        startPosZ: Float,
        endPosX: Float,
        endPosY: Float,
        endPosZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    )
}
