package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.CTPlayer
import com.chattriggers.ctjs.api.entity.PlayerMP
import com.chattriggers.ctjs.api.render.renderstates.GUIRenderState
import com.chattriggers.ctjs.api.render.renderstates.GradientGUIRenderState
import com.chattriggers.ctjs.api.render.renderstates.TexturedGUIRenderState
import com.chattriggers.ctjs.engine.LogType
import com.chattriggers.ctjs.engine.printToConsole
import com.chattriggers.ctjs.internal.utils.getOrDefault
import com.chattriggers.ctjs.internal.utils.toRadians
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.AddressMode
import com.mojang.blaze3d.textures.FilterMode
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import com.mojang.blaze3d.vertex.PoseStack
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.Component
import org.joml.Matrix3x2f
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.mozilla.javascript.NativeObject
import kotlin.math.atan

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//$$import net.minecraft.client.gui.render.state.GuiTextRenderState
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.SubmitNodeStorage
import net.minecraft.client.renderer.state.gui.GuiTextRenderState
//#endif

object GUIRenderer : BaseGUIRenderer() {
    private lateinit var slimCTRenderPlayer: CTPlayerRenderer
    private lateinit var normalCTRenderPlayer: CTPlayerRenderer

    @JvmStatic
    var partialTicks = 0f
        internal set

    @JvmStatic
    fun initializePlayerRenderers(context: EntityRendererProvider.Context) {
        normalCTRenderPlayer = CTPlayerRenderer(context, slim = false)
        slimCTRenderPlayer = CTPlayerRenderer(context, slim = true)
    }

    override fun drawString(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long,
        textScale: Float,
        renderBackground: Boolean,
        textShadow: Boolean,
        maxWidth: Int,
        zOffset: Float
    ) {
        drawText(drawContext, Component.literal(text), xPosition, yPosition, color, textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextWithShadowRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextWithShadow(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTextRGBAArray(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component, xPosition: Float, yPosition: Float, colorArray: IntArray = intArrayOf(255, 255, 255, 255), textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f) {
        drawText(drawContext, text, xPosition, yPosition, RenderUtils.RGBAColor.fromIntArray(colorArray).getLongRGBA(), textScale, renderBackground, textShadow, maxWidth, zOffset)
    }

    @JvmStatic
    @JvmOverloads
    fun drawText(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        text: Component,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f, // Useless in 1.21.6+, text is drawn on top of all elements
    ) {
        val (a, r, g, b) = RenderUtils.RGBAColor.fromLongRGBA(color).getIntComponentsARGB()
        if (a == 0) return
        val safeAlpha = if (a in 1..3) 4 else a
        val safeColorIntARGB = RenderUtils.ARGBColor(r, g, b, safeAlpha).getIntARGB()
        val backgroundColor = if (renderBackground) {
            RenderUtils.ARGBColor(0, 0, 0, 150)
        } else {
            RenderUtils.ARGBColor(0, 0, 0, 0)
        }
        val backgroundColorInt = backgroundColor.getIntARGB()

        val textRenderer = RenderUtils.getTextRenderer()
        var currentY = 0f
        val lines = RenderUtils.splitText(text, maxWidth).lines

        val backgroundColorLong = backgroundColor.getLongRGBA()
        lines.forEach { line ->
            val matrix = Matrix3x2f(drawContext.pose())
            matrix.translate(xPosition, yPosition + currentY)
            matrix.scale(textScale, textScale)

            if (renderBackground) {
                val textWidth = textRenderer.width(line)
                drawRect(
                    drawContext,
                    xPosition - (1f * textScale),
                    yPosition + currentY - (1f * textScale),
                    (textWidth + 1f) * textScale,
                    (textRenderer.lineHeight + 1f) * textScale,
                    backgroundColorLong,
                    0f,
                )
            }

            val textState = GuiTextRenderState(
                textRenderer,
                line.visualOrderText,
                matrix,
                0,
                0,
                safeColorIntARGB,
                backgroundColorInt,
                textShadow,
                //#if MC>=12111
                true,
                //#endif
                drawContext.scissorStack.peek()
            )
            //#if MC<=12111
            //$$drawContext.guiRenderState.submitText(textState)
            //#else
            drawContext.guiRenderState.addText(textState)
            //#endif
            currentY += textRenderer.lineHeight * textScale
        }
    }

    override fun _drawLine(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawRect(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawRoundedRect(
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
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = listOf(
            Pair(x1, y1),
            Pair(x2, y1),
            Pair(x2, y2),
            Pair(x1, y2)
        )

        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawGradient(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        vertexAndColorList: List<Triple<Float, Float, Long>>,
        zOffset: Float,
    ) {
        val boundsList = vertexAndColorList.map { (x, y, _) -> Pair(x, y) }
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GradientGUIRenderState(
                GUIRenderState(
                    RenderUtils.matrixStack.to3x2Joml(),
                    listOf(),
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor(255, 255, 255, 255),
                    RenderPipelines.QUADS().build(),
                    drawContext.scissorStack.peek(),
                ),
                vertexAndColorList,
            )
        )
    }

    override fun _drawCircle(
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
        color: Long,
        zOffset: Float,
    ) {
        val boundsList = listOf(
            Pair(minX, minY),
            Pair(maxX, minY),
            Pair(maxX, maxY),
            Pair(minX, maxY)
        )

        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            GUIRenderState(
                RenderUtils.matrixStack.to3x2Joml(),
                vertexList,
                boundsList,
                zOffset,
                RenderUtils.RGBAColor.fromLongRGBA(color),
                RenderPipelines.QUADS().build(),
                drawContext.scissorStack.peek(),
            )
        )
    }

    override fun _drawImage(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        image: Image,
        texture: DynamicTexture,
        vertexList: List<Pair<Float, Float>>,
        uvList: List<Pair<Float, Float>>,
        color: Long,
        zOffset: Float,
    ) {
        val sampler = RenderSystem.getSamplerCache().getSampler(
            AddressMode.CLAMP_TO_EDGE,
            AddressMode.CLAMP_TO_EDGE,
            FilterMode.LINEAR,
            FilterMode.NEAREST,
            false
        )
        val boundsList = vertexList.toList()
        //#if MC<=12111
        //$$drawContext.guiRenderState.submitGuiElement(
        //#else
        drawContext.guiRenderState.addGuiElement(
            //#endif
            TexturedGUIRenderState(
                GUIRenderState(
                    RenderUtils.matrixStack.to3x2Joml(),
                    vertexList,
                    boundsList,
                    zOffset,
                    RenderUtils.RGBAColor.fromLongRGBA(color),
                    RenderPipelines.TEXTURED_QUADS().build(),
                    drawContext.scissorStack.peek()
                ),
                TextureSetup.singleTexture(texture.textureView, sampler),
                uvList,
            )
        )
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
            it as? AbstractClientPlayer
                ?: ((it as? PlayerMP)?.toMC() as? AbstractClientPlayer)
                ?: CTPlayer.toMC()
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
            val mouseY = y - Client.getMouseY() - (entity.eyeHeight * size)
            atan((mouseX / 40.0f)).toFloat() to atan((mouseY / 40.0f)).toFloat()
        } else {
            val scaleFactor = 130f / 180f
            (yaw * scaleFactor).toRadians() to pitch.toRadians()
        }

        val flipModelRotation = Quaternionf().rotateZ(Math.PI.toFloat())
        val pitchModelRotation = Quaternionf().rotateX(entityPitch * 20.0f * (Math.PI / 180.0).toFloat())
        flipModelRotation.mul(pitchModelRotation)

        val oldBodyYaw = entity.yBodyRot
        val oldYaw = entity.yRot
        val oldPitch = entity.xRot
        val oldPrevHeadYaw = entity.yHeadRotO
        val oldHeadYaw = entity.yHeadRot

        entity.yBodyRot = 180.0f + entityYaw * 20.0f
        entity.setYRot(180.0f + entityYaw * 40.0f)
        entity.setXRot(-entityPitch * 20.0f)
        entity.yHeadRot = entity.yRot
        entity.yHeadRotO = entity.yRot

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

        val entityRenderDispatcher = Client.getMinecraft().entityRenderDispatcher

        if (pitchModelRotation != null) {
            pitchModelRotation.conjugate()
            entityRenderDispatcher.camera?.rotation()?.set(pitchModelRotation)
        }

        //#if MC<26.2
        //$$val vertexConsumers = Client.getMinecraft().renderBuffers().bufferSource()
        //#else
        val vertexConsumers = Client.getMinecraft().gameRenderer.renderBuffers().stagedVertexBuffer()
        //#endif

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
            this.scale = size.toFloat()
            this.bodyRot = entity.yBodyRot
            this.yRot = entity.yRot
        }

        val vec3d = entityRenderer.getRenderOffset(playerEntityRenderState)
        val d = vec3d.x()
        val e = vec3d.y()
        val f = vec3d.z()
        RenderUtils.matrixStack.push()
        RenderUtils.matrixStack.translate(d, e, f)

        entityRenderer.submit(
            playerEntityRenderState,
            RenderUtils.matrixStack.toMC(),
            //#if MC<=12111
            //$$Client.getMinecraft().gameRenderer.submitNodeStorage,
            //#else
            SubmitNodeStorage(),
            //#endif
            //#if MC<=12111
            //$$Client.getMinecraft().gameRenderer.levelRenderState.cameraRenderState
            //#elseif MC<26.2
            //$$Client.getMinecraft().gameRenderer.gameRenderState.levelRenderState.cameraRenderState
            //#else
            Client.getMinecraft().gameRenderer.gameRenderState().levelRenderState.cameraRenderState
            //#endif
        )

        RenderUtils.matrixStack.pop()
        //#if MC<26.2
        //$$vertexConsumers.endBatch()
        //#else
        vertexConsumers.endFrame()
        //#endif

        RenderUtils.matrixStack.pop()
        RenderUtils.matrixStack.pop()

        entity.yBodyRot = oldBodyYaw
        entity.yRot = oldYaw
        entity.xRot = oldPitch
        entity.yHeadRotO = oldPrevHeadYaw
        entity.yHeadRot = oldHeadYaw

        RenderUtils.matrixStack.pop()
    }

    internal fun withMatrix(stack: PoseStack?, partialTicks: Float = GUIRenderer.partialTicks, block: () -> Unit) {
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
}
