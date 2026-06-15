package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.Player
import com.chattriggers.ctjs.api.entity.PlayerMP
import com.chattriggers.ctjs.api.render.RenderUtils.getColorRGBA
import com.chattriggers.ctjs.engine.LogType
import com.chattriggers.ctjs.engine.printToConsole
import com.chattriggers.ctjs.internal.utils.getOrDefault
import com.chattriggers.ctjs.internal.utils.toRadians
import gg.essential.universal.UMatrixStack
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.mozilla.javascript.NativeObject
import java.awt.Color
import java.util.Collections
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//#if MC<=12108
//$$import net.minecraft.client.render.DiffuseLighting
//$$import com.chattriggers.ctjs.internal.mixins.EntityRenderDispatcherAccessor
//$$import com.chattriggers.ctjs.internal.utils.asMixin
//$$import org.joml.Matrix3x2fStack
//#endif

object GUIRenderer {
    private lateinit var slimCTRenderPlayer: CTPlayerRenderer
    private lateinit var normalCTRenderPlayer: CTPlayerRenderer

    @JvmField
    val screen = ScreenWrapper()

    // The current partialTicks value
    @JvmStatic
    var partialTicks = 0f
        internal set

    @JvmStatic
    fun initializePlayerRenderers(context: EntityRendererFactory.Context) {
        normalCTRenderPlayer = CTPlayerRenderer(context, slim = false)
        slimCTRenderPlayer = CTPlayerRenderer(context, slim = true)
    }

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
    @JvmStatic
    @JvmOverloads
    fun drawSquareRGBA(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, RenderUtils.getColor(red, green, blue, alpha))
    }

    /**
     * Draws a square to the screen
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param size the size of the square
     * @param color the color as a [Long] value in RGBA format
     */
    @JvmStatic
    @JvmOverloads
    fun drawSquare(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
    ) {
        drawRect(drawContext, xPosition, yPosition, size, size, color)
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
    @JvmStatic
    @JvmOverloads
    fun drawRectRGBA(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
    ) {
        drawRect(drawContext, xPosition, yPosition, width, height, RenderUtils.getColor(red, green, blue, alpha))
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
    @JvmStatic
    @JvmOverloads
    fun drawRect(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
    ) {
        val pos = mutableListOf(xPosition, yPosition, xPosition + width, yPosition + height)
        if (pos[0] > pos[2]) Collections.swap(pos, 0, 2)
        if (pos[1] > pos[3]) Collections.swap(pos, 1, 3)

        RenderUtils
            .begin(CTRenderLayers.CT_QUADS_ESP())
            .colorize(color)
            .cameraPos(pos[0], pos[3], 0f)
            .cameraPos(pos[2], pos[3], 0f)
            .cameraPos(pos[2], pos[1], 0f)
            .cameraPos(pos[0], pos[1], 0f)
            .draw()
    }

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
    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(
        drawContext: DrawContext,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        lineThickness: Float = 1f,
    ) {
        drawLine(drawContext, startX, startY, endX, endY, RenderUtils.getColor(red, green, blue, alpha), lineThickness)
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
    @JvmStatic
    @JvmOverloads
    fun drawLine(
        drawContext: DrawContext,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
    ) {
        val theta = -atan2(endY - startY, endX - startX)
        val i = sin(theta) * (lineThickness / 2)
        val j = cos(theta) * (lineThickness / 2)

        RenderUtils
            .begin(CTRenderLayers.CT_QUADS_ESP())
            .colorize(color)
            .cameraPos(startX + i, startY + j, 0f)
            .cameraPos(endX + i, endY + j, 0f)
            .cameraPos(endX - i, endY - j, 0f)
            .cameraPos(startX - i, startY - j, 0f)
            .draw()
    }

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
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircleRGBA(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, RenderUtils.getColor(red, green, blue, alpha), edges, rotationDegrees, xRotationOffset, yRotationOffset)
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
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCircle(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
    ) {
        drawCircle(drawContext, xPosition, yPosition, radius, radius, color, edges, rotationDegrees, xRotationOffset, yRotationOffset)
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
    @JvmStatic
    @JvmOverloads
    fun drawCircleRGBA(
        drawContext: DrawContext,
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
    ) {
        drawCircle(drawContext, xPosition, yPosition, xScale, yScale, RenderUtils.getColor(red, green, blue, alpha), edges, rotationDegrees, xRotationOffset, yRotationOffset)
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
    @JvmStatic
    @JvmOverloads
    fun drawCircle(
        drawContext: DrawContext,
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
    ) {
        val theta = 2 * PI / edges
        val cos = cos(theta).toFloat()
        val sin = sin(theta).toFloat()

        var xHolder: Float
        var circleX = 1f
        var circleY = 0f

        // rotation from circle's center
        RenderUtils
            .pushMatrix()
            .translate(xPosition + xRotationOffset, yPosition + yRotationOffset, 0f)
            .rotate(rotationDegrees % 360, 0f, 0f, 1f)
            .translate(-xPosition + -xRotationOffset, -yPosition + -yRotationOffset, 0f)
            .begin(CTRenderLayers.CT_TRIANGLE_STRIP_ESP())
            .colorize(color)

        for (i in 0..edges) {
            RenderUtils
                .cameraPos(xPosition, yPosition, 0f)
                .cameraPos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
            xHolder = circleX
            circleX = cos * circleX - sin * circleY
            circleY = sin * xHolder + cos * circleY

            RenderUtils.cameraPos(circleX * xScale + xPosition, circleY * yScale + yPosition, 0f)
        }

        RenderUtils
            .draw()
            .popMatrix()
    }

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
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadowRGBA(
        drawContext: DrawContext,
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
    ) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.getColor(red, green, blue, alpha), textScale, renderBackground, true)
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
    @JvmStatic
    @JvmOverloads
    fun drawStringWithShadow(
        drawContext: DrawContext,
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
    ) {
        drawString(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true)
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
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(
        drawContext: DrawContext,
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
    ) {
        drawString(drawContext, text, xPosition, yPosition, RenderUtils.getColor(red, green, blue, alpha), textScale, renderBackground, textShadow)
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
    @JvmStatic
    @JvmOverloads
    fun drawString(
        drawContext: DrawContext,
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
    ) {
        val fontRenderer = RenderUtils.getFontRenderer()
        var newY = yPosition

        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            Color(0, 0, 0, 0).rgb
        }

        val (r, g, b, a) = getColorRGBA(color)
        val colorInt = Color(Color(r, g, b, a).rgb, true).rgb

        // scale from text's center
        RenderUtils
            .pushMatrix()
            .translate(xPosition, yPosition, 0f)
            .scale(textScale, textScale, 1f)
            .translate(-xPosition, -yPosition, 0f)

        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers
        RenderUtils.splitText(text).lines.forEach {
            fontRenderer.draw(
                it,
                xPosition,
                newY,
                colorInt,
                textShadow,
                RenderUtils.matrixStack.toMC().peek().positionMatrix,
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                backgroundColorInt,
                0xF000F0,
            )

            newY += fontRenderer.fontHeight
        }

        vertexConsumers.draw()
        RenderUtils.popMatrix()
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
    @JvmStatic
    @JvmOverloads
    fun drawImage(
        drawContext: DrawContext,
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
    ) {
        val texture = image.getTexture() ?: return

        val identifier = image.getIdOrRegister()
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        RenderUtils
            .pushMatrix()
            //#if MC<=12105
            //$$.setShaderTexture(0, texture.glTexture)
            //#else
            //#if MC<=12110
            //$$.setShaderTexture(0, texture.glTextureView)
            //#else
            .setShaderTexture(texture.glTextureView)
            //#endif
            //#endif

            .scale(1f, 1f, 50f)
            .begin(CTRenderLayers.CT_TEXTURED_QUADS_ESP(textureIdentifier = identifier))
            .colorize(1f, 1f, 1f, 1f)
            .cameraPos(xPosition, yPosition + drawHeight, 0f).tex(0f, 1f)
            .cameraPos(xPosition + drawWidth, yPosition + drawHeight, 0f).tex(1f, 1f)
            .cameraPos(xPosition + drawWidth, yPosition, 0f).tex(1f, 0f)
            .cameraPos(xPosition, yPosition, 0f).tex(0f, 0f)
            .draw()
            .popMatrix()
    }

    /**
     * Draws a player entity to the screen, similar to the one displayed in the inventory screen.
     *
     * Takes a parameter with the following options:
     * - player: The player entity to draw. Can be a [PlayerMP] or [AbstractClientPlayerEntity].
     *           Defaults to Player.toMC()
     * - x: The x position on the screen to render the player
     * - y: The y position on the screen to render the player
     * - size: The size of the rendered player
     * - rotate: Whether the player should look at the mouse cursor, similar to the inventory screen
     * - pitch: THe pitch the rendered player will face, if rotate is false
     * - yaw: The yaw the rendered player will face, if rotate is false
     * - showNametag: Whether the nametag of the player should be rendered
     * - showArmor: Whether the armor of the player should be rendered
     * - showCape: Whether the cape of the player should be rendered
     * - showHeldItem: Whether the held item of the player should be rendered
     * - showArrows: Whether any arrows stuck in the player's model should be rendered
     * - showElytra: Whether the player's Elytra should be rendered
     * - showParrot: Whether a perched parrot should be rendered
     * - showBeeStinger: Whether any stuck bee stingers should be rendered
     *
     * @param obj An options bag
     */
    @JvmStatic
    fun drawPlayer(obj: NativeObject) {
        val entity = obj["player"].let {
            it as? AbstractClientPlayerEntity
                ?: ((it as? PlayerMP)?.toMC() as? AbstractClientPlayerEntity)
                ?: Player.toMC()
                ?: return
        }

        val x = obj.getOrDefault<Number>("x", 0).toInt()
        val y = obj.getOrDefault<Number>("y", 0).toInt()
        val size = obj.getOrDefault<Number>("size", 20).toDouble()
        val rotate = obj.getOrDefault<Boolean>("rotate", false)
        val pitch = obj.getOrDefault<Number>("pitch", 0f).toFloat()
        val yaw = obj.getOrDefault<Number>("yaw", 0f).toFloat()
        val slim = obj.getOrDefault<Boolean>("slim", false)
        val showNametag = obj.getOrDefault<Boolean>("showNametag", false)
        val showArmor = obj.getOrDefault<Boolean>("showArmor", false)
        val showCape = obj.getOrDefault<Boolean>("showCape", false)
        val showHeldItem = obj.getOrDefault<Boolean>("showHeldItem", false)
        val showArrows = obj.getOrDefault<Boolean>("showArrows", false)
        val showElytra = obj.getOrDefault<Boolean>("showElytra", false)
        val showParrot = obj.getOrDefault<Boolean>("showParrot", false)
        val showStingers = obj.getOrDefault<Boolean>("showBeeStinger", false)

        RenderUtils.matrixStack.push()

        val (entityYaw, entityPitch) = if (rotate) {
            val mouseX = x - Client.getMouseX()
            val mouseY = y - Client.getMouseY() - (entity.standingEyeHeight * size)
            atan((mouseX / 40.0f)).toFloat() to atan((mouseY / 40.0f)).toFloat()
        } else {
            val scaleFactor = 130f / 180f
            (yaw * scaleFactor).toRadians() to pitch.toRadians()
        }

        val flipModelRotation = Quaternionf().rotateZ(Math.PI.toFloat())
        val pitchModelRotation = Quaternionf().rotateX(entityPitch * 20.0f * (Math.PI / 180.0).toFloat())
        flipModelRotation.mul(pitchModelRotation)

        val oldBodyYaw = entity.bodyYaw
        val oldYaw = entity.yaw
        val oldPitch = entity.pitch
        val oldPrevHeadYaw = entity.lastHeadYaw
        val oldHeadYaw = entity.headYaw

        entity.bodyYaw = 180.0f + entityYaw * 20.0f
        entity.yaw = 180.0f + entityYaw * 40.0f
        entity.pitch = -entityPitch * 20.0f
        entity.headYaw = entity.yaw
        entity.lastHeadYaw = entity.yaw

        RenderUtils.matrixStack.push()
        RenderUtils.matrixStack.translate(0.0, 0.0, 1000.0)
        RenderUtils.matrixStack.push()
        RenderUtils.matrixStack.translate(x.toDouble(), y.toDouble(), 50.0)

        // UC's version of multiplyPositionMatrix
        RenderUtils.matrixStack.peek().model.mul(
            Matrix4f().scaling(
                size.toFloat(),
                size.toFloat(),
                (-size).toFloat(),
            ),
        )

        RenderUtils.matrixStack.multiply(flipModelRotation)
        //#if MC<=12105
        //$$DiffuseLighting.enableGuiShaderLighting()
        //#endif

        val entityRenderDispatcher = Client.getMinecraft().entityRenderDispatcher

        if (pitchModelRotation != null) {
            pitchModelRotation.conjugate()
            //#if MC<=12108
            //$$entityRenderDispatcher.rotation = pitchModelRotation
            //#else
            entityRenderDispatcher.camera?.rotation?.set(pitchModelRotation)
            //#endif
        }

        //#if MC<=12108
        //$$entityRenderDispatcher.setRenderShadows(false)
        //$$val light = 0xF000F0
        //#endif
        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers

        val entityRenderer = if (slim) slimCTRenderPlayer else normalCTRenderPlayer
        entityRenderer.setOptions(
            showNametag,
            showArmor,
            showCape,
            showHeldItem,
            showArrows,
            showElytra,
            showParrot,
            showStingers,
        )

        val playerEntityRenderState = entityRenderer.createRenderState().apply {
            this.baseScale = size.toFloat()
            this.bodyYaw = entity.bodyYaw
            this.relativeHeadYaw = entity.yaw
        }

        val vec3d = entityRenderer.getPositionOffset(playerEntityRenderState)
        val d = vec3d.getX()
        val e = vec3d.getY()
        val f = vec3d.getZ()
        RenderUtils.matrixStack.push()
        RenderUtils.matrixStack.translate(d, e, f)

        //#if MC<=12108
        //$$entityRenderer.render(playerEntityRenderState, RenderUtils.matrixStack.toMC(), vertexConsumers, light)
        //$$if (entity.doesRenderOnFire()) {
        //$$    entityRenderDispatcher
        //$$        .asMixin<EntityRenderDispatcherAccessor>()
        //$$        .invokerRenderFire(RenderUtils.matrixStack.toMC(), vertexConsumers, playerEntityRenderState, Quaternionf())
        //$$}
        //#else
        entityRenderer.render(
            playerEntityRenderState,
            RenderUtils.matrixStack.toMC(),
            Client.getMinecraft().gameRenderer.entityRenderCommandQueue,
            Client.getMinecraft().gameRenderer.entityRenderStates.cameraRenderState
        )
        //#endif

        RenderUtils.matrixStack.pop()
        vertexConsumers.draw()
        //#if MC<=12108
        //$$entityRenderDispatcher.setRenderShadows(true)
        //#endif

        RenderUtils.matrixStack.pop()
        //#if MC<=12105
        //$$DiffuseLighting.enableGuiDepthLighting()
        //#endif
        RenderUtils.matrixStack.pop()

        entity.bodyYaw = oldBodyYaw
        entity.yaw = oldYaw
        entity.pitch = oldPitch
        entity.lastHeadYaw = oldPrevHeadYaw
        entity.headYaw = oldHeadYaw

        RenderUtils.matrixStack.pop()
    }

    internal fun withMatrix(stack: MatrixStack?, partialTicks: Float = GUIRenderer.partialTicks, block: () -> Unit) {
        GUIRenderer.partialTicks = partialTicks
        RenderUtils.matrixPushCounter = 0

        try {
            if (stack != null) RenderUtils.pushMatrix(UMatrixStack(stack))
            block()
        } finally {
            if (stack != null) RenderUtils.popMatrix()
        }

        if (RenderUtils.matrixPushCounter > 0) {
            "Warning: Render function missing a call to RenderUtils.popMatrix()".printToConsole(LogType.WARN)
        } else if (RenderUtils.matrixPushCounter < 0) {
            "Warning: Render function has too many calls to RenderUtils.popMatrix()".printToConsole(LogType.WARN)
        }
    }

    class ScreenWrapper {
        fun getWidth(): Int = Client.getMinecraft().window.scaledWidth

        fun getHeight(): Int = Client.getMinecraft().window.scaledHeight

        fun getScale(): Double = Client.getMinecraft().window.scaleFactor.toDouble()
    }
}
