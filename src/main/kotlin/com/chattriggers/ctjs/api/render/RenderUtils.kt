package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.CTPlayer
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.vec.Vec3f
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos
import com.chattriggers.ctjs.api.render.VertexFormat as CTVertexFormat
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.network.chat.Component
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.Optional
import net.minecraft.network.chat.Style

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Camera
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult

import com.mojang.blaze3d.textures.GpuTextureView
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Matrix3x2fStack
import org.joml.Matrix3x2f
import org.joml.Matrix4f

import gg.essential.universal.UMatrixStack

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//$$import com.mojang.blaze3d.platform.DepthTestFunction
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.platform.CompareOp
import kotlin.ranges.coerceIn
//#endif

//#if MC<26.2
//$$import com.mojang.blaze3d.platform.DestFactor
//$$import com.mojang.blaze3d.platform.SourceFactor
//$$import com.mojang.blaze3d.vertex.Tesselator
//$$import com.mojang.blaze3d.vertex.BufferBuilder
//#else
import com.mojang.blaze3d.platform.BlendFactor
import com.mojang.blaze3d.PrimitiveTopology
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.StagedVertexBuffer
//#endif

object RenderUtils {
    @JvmStatic
    fun getRenderManager() = Client.getMinecraft().levelRenderer
    private val NEWLINE_REGEX = """\n|\r\n?""".toRegex()

    @JvmField
    val screen = ScreenWrapper()

    @JvmStatic
    fun getTextRenderer(): net.minecraft.client.gui.Font = Client.getMinecraft().font

    @JvmField
    var colorized: Long? = null

    @JvmField
    var vertexColor: Color? = null

    private var firstVertex = true
    private var began = false

    // The currently-active matrix stack
    internal lateinit var matrixStack: UMatrixStack
    private val matrixStackStack = ArrayDeque<UMatrixStack>()
    internal var matrixPushCounter = 0

    @JvmStatic
    fun setMatrixStack(stack: UMatrixStack) = apply {
        matrixStack = stack
    }
    @JvmStatic
    fun setMatrixStack(stack: PoseStack) = apply {
        matrixStack = UMatrixStack(stack)
    }
    fun setMatrixStack(stack: Matrix3x2fStack) = apply {
        matrixStack = UMatrixStack(stack)
    }

    // Camera
    @JvmStatic
    fun getCamera(): Camera {
        //#if MC<26.2
        //$$return Client.getMinecraft().gameRenderer.mainCamera
        //#else
        return Client.getMinecraft().gameRenderer.mainCamera()
        //#endif
    }

    @JvmStatic
    fun getCameraPos(): Vec3 {
        return getCameraPos(getCamera())
    }
    @JvmStatic
    fun getCameraPos(camera: Camera): Vec3 {
        return camera.position()
    }

    // Pipeline
    private var renderLayer: RenderType? = null
    private var vertexFormat: VertexFormat? = null
    //#if MC<26.2
    //$$private var instance: BufferBuilder? = null
    //#else
    private var instance: VertexConsumer? = null
    private var activeDraw: StagedVertexBuffer.Draw? = null
    //#endif

    fun beginRenderLayer(renderLayer: RenderType) = apply {
        this.renderLayer = renderLayer
        //#if MC<26.2
        //$$beginInternal(renderLayer.mode(), renderLayer.format())
        //#else
        beginInternal(renderLayer.primitiveTopology(), renderLayer.format())
        //#endif
    }
    //#if MC<26.2
    //$$private fun beginInternal(mode: VertexFormat.Mode, format: VertexFormat) = apply {
    //#else
    private fun beginInternal(mode: PrimitiveTopology, format: VertexFormat) = apply {
    //#endif
        vertexFormat = format
        //#if MC<26.2
        //$$instance = Tesselator.getInstance().begin(mode, format)
        //#else
        val stagedVertexBuffer = Client.getMinecraft().gameRenderer.renderBuffers().stagedVertexBuffer()
        val draw = stagedVertexBuffer.appendDraw(format, mode)
        activeDraw = draw
        instance = stagedVertexBuffer.getVertexBuilder(draw)
        //#endif
    }
    private fun drawDirect() = apply {
        //#if MC<26.2
        //$$val builtBuffer = instance?.build() ?: return@apply
        //$$renderLayer?.draw(builtBuffer)
        //#else
        val draw = activeDraw ?: return@apply
        val layer = renderLayer ?: return@apply
        val stagedVertexBuffer = Client.getMinecraft().gameRenderer.renderBuffers().stagedVertexBuffer()
        stagedVertexBuffer.upload()
        val executeInfo = stagedVertexBuffer.getExecuteInfo(draw)
        if (executeInfo != null) {
            layer.prepare().drawFromBuffer(executeInfo)
        }
        stagedVertexBuffer.endDraw()
        activeDraw = null
        //#endif
    }
    private fun posInternal(stack: UMatrixStack, x: Float, y: Float, z: Float) = apply {
        instance?.addVertex(stack.peek().model, x, y, z)
    }
    private fun posInternal(stack: UMatrixStack, x: Double, y: Double, z: Double) = apply {
        posInternal(stack, x.toFloat(), y.toFloat(), z.toFloat())
    }
    private fun texInternal(u: Float, v: Float) = apply {
        instance?.setUv(u, v)
    }
    private fun texInternal(u: Double, v: Double) = apply {
        texInternal(u.toFloat(), v.toFloat())
    }
    private fun normInternal(stack: UMatrixStack, x: Float, y: Float, z: Float) = apply {
        val normal = stack.peek().normal.transform(x, y, z, Vector3f())
        instance?.setNormal(normal.x(), normal.y(), normal.z())
    }
    private fun normInternal(stack: UMatrixStack, x: Double, y: Double, z: Double) = apply {
        normInternal(stack, x.toFloat(), y.toFloat(), z.toFloat())
    }
    private fun overlayInternal(u: Int, v: Int) = apply {
        instance?.setUv1(u, v)
    }
    private fun lightInternal(u: Int, v: Int) = apply {
        instance?.setUv2(u, v)
    }
    private fun colorInternal(red: Float, green: Float, blue: Float, alpha: Float) = apply {
        instance?.setColor(red, green, blue, alpha)
    }
    private fun lineWidthInternal(width: Double) = apply {
        lineWidthInternal(width.toFloat())
    }
    private fun lineWidthInternal(width: Float) = apply {
        //#if MC>=12111
        instance?.setLineWidth(width)
        //#endif
    }

    // Colors
    @JvmField val BLACK = RGBAColor(0, 0, 0, 255).getLong()
    @JvmField val DARK_BLUE = RGBAColor(0, 0, 190, 255).getLong()
    @JvmField val DARK_GREEN = RGBAColor(0, 190, 0, 255).getLong()
    @JvmField val DARK_AQUA = RGBAColor(0, 190, 190, 255).getLong()
    @JvmField val DARK_RED = RGBAColor(190, 0, 0, 255).getLong()
    @JvmField val DARK_PURPLE = RGBAColor(190, 0, 190, 255).getLong()
    @JvmField val GOLD = RGBAColor(217, 163, 52, 255).getLong()
    @JvmField val GRAY = RGBAColor(190, 190, 190, 255).getLong()
    @JvmField val DARK_GRAY = RGBAColor(63, 63, 63, 255).getLong()
    @JvmField val BLUE = RGBAColor(63, 63, 254, 255).getLong()
    @JvmField val GREEN = RGBAColor(63, 254, 63, 255).getLong()
    @JvmField val AQUA = RGBAColor(63, 254, 254, 255).getLong()
    @JvmField val RED = RGBAColor(254, 63, 63, 255).getLong()
    @JvmField val LIGHT_PURPLE = RGBAColor(254, 63, 254, 255).getLong()
    @JvmField val YELLOW = RGBAColor(254, 254, 63, 255).getLong()
    @JvmField val WHITE = RGBAColor(255, 255, 255, 255).getLong()

    @JvmStatic
    fun color(color: Int): Long = when (color) {
        0 -> BLACK
        1 -> DARK_BLUE
        2 -> DARK_GREEN
        3 -> DARK_AQUA
        4 -> DARK_RED
        5 -> DARK_PURPLE
        6 -> GOLD
        7 -> GRAY
        8 -> DARK_GRAY
        9 -> BLUE
        10 -> GREEN
        11 -> AQUA
        12 -> RED
        13 -> LIGHT_PURPLE
        14 -> YELLOW
        else -> WHITE
    }

    @JvmStatic
    fun colorInt(color: Long): Int = color.toInt()

    @JvmStatic
    fun getFormattedTextFromString(text: String): Component = Component.literal(ChatLib.addColor(text))

    @JvmStatic
    fun getStringWidth(text: Component): Int = getTextRenderer().width(text)

    @JvmStatic
    fun getStringWidth(text: String): Int {
        return Client.synchronizedTask {
            getStringWidth(getFormattedTextFromString(text))
        }
    }

    @JvmStatic
    fun baseStartDraw() = apply {
        pushMatrix()
            .disableCull()
            .enableBlend()
            .tryBlendFuncSeparate(
                770, // SRC_ALPHA
                771, // ONE_MINUS_SRC_ALPHA
                1,  // ONE
                0,  // ZERO
            )
    }
    @JvmStatic
    fun guiStartDraw() = apply {
        baseStartDraw()
            .depthMask(false)
            .enableLineSmooth()
            .disableTexture2D()
    }

    @JvmStatic
    fun baseEndDraw() = apply {
        enableCull()
            .enableDepth()
            .disableLineSmooth()
            .resetLineWidth()
            .disableBlend()
            .resetColor()
            .popMatrix()
    }
    @JvmStatic
    fun guiEndDraw() = apply {
        depthMask(true)
            .enableTexture2D()
            .baseEndDraw()
    }
    @JvmStatic
    fun worldEndDraw() = apply {
        enableTexture2D()
            .depthMask(true)
            .baseEndDraw()
    }

    private fun _begin() = apply {
        colorized = null
        vertexColor = null
        firstVertex = true
        began = true
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param renderLayer The [RenderType] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(renderLayer: RenderType = RenderLayers.QUADS()) = apply {
        _begin()
        beginRenderLayer(renderLayer)
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: CTVertexFormat = CTVertexFormat.POSITION_COLOR,
    ) = apply {
        RenderLayers.getRenderLayer(drawMode, vertexFormat)?.let { renderLayer ->
            begin(renderLayer)
        }
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @param snippet The [RenderSnippet] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: CTVertexFormat = CTVertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        begin(PipelineBuilder.begin(drawMode, vertexFormat, snippet).layer())
    }

    /**
     * Finalizes vertices and draws the world renderer.
     */
    @JvmStatic
    fun draw(): RenderUtils = apply {
        if (!began) return this
        began = false

        endVertex()
        drawDirect()
    }

    @JvmStatic
    fun endVertex() = apply { }

    /**
     * Sets a new vertex in the world renderer.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun pos(x: Float, y: Float, z: Float, endVertex: Boolean = true) = apply {
        pos(x.toDouble(), y.toDouble(), z.toDouble(), endVertex)
    }
    @JvmStatic
    fun pos(x: Double, y: Double, z: Double, endVertex: Boolean = true) = apply {
        if (!began) begin()
        if (!firstVertex && endVertex) endVertex()

        val cameraPos = getCameraPos()
        posInternal(matrixStack, x - cameraPos.x, y - cameraPos.y, z - cameraPos.z)

        firstVertex = false
        vertexColor?.let {
            color(vertexColor!!)
        }
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y, z) vertices in List<Pair<Float, Float, Float>> format
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun posList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, pos.third)
        }
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y) vertices in List<Pair<Float, Float>> format
     * @param zPosition the z position for all vertices
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun posList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, zPosition)
        }
    }

    /**
     * Sets a new vertex in the world renderer in screen space.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Float, y: Float, z: Float = 0f, endVertex: Boolean = true) = apply {
        cameraPos(x.toDouble(), y.toDouble(), z.toDouble(), endVertex)
    }
    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Double, y: Double, z: Double = 0.0, endVertex: Boolean = true) = apply {
        val cameraPos = getCameraPos()
        pos(x + cameraPos.x, y + cameraPos.y, z + cameraPos.z, endVertex)
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y, z) vertices in List<Pair<Float, Float, Float>> format
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun cameraPosList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, pos.third)
        }
    }
    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y) vertices in List<Pair<Float, Float>> format
     * @param zPosition the z position for all vertices
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun cameraPosList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, zPosition)
        }
    }

    /**
     * Sets the texture location on the last defined vertex.
     *
     * @param u the u position in the texture
     * @param v the v position in the texture
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun tex(u: Float, v: Float) = apply {
        tex(u.toDouble(), v.toDouble())
    }
    @JvmStatic
    fun tex(u: Double, v: Double) = apply {
        texInternal(u, v)
    }

    /**
     * Sets the normal of the vertex. This is mostly used with [VertexFormat.LINES]
     *
     * @param x the x position of the normal vector
     * @param y the y position of the normal vector
     * @param z the z position of the normal vector
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun normal(x: Float, y: Float, z: Float) = apply {
        normal(x.toDouble(), y.toDouble(), z.toDouble())
    }
    @JvmStatic
    fun normal(x: Double, y: Double, z: Double) = apply {
        normInternal(matrixStack, x, y, z)
    }
    @JvmStatic
    fun normal(vector: Vector3f?) = apply {
        vector?.let {
            normal(it.x, it.y, it.z)
        }
    }

    /**
     * Sets the overlay location on the last defined vertex.
     *
     * @param u the u position in the overlay
     * @param v the v position in the overlay
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun overlay(u: Int, v: Int) = apply {
        overlayInternal(u, v)
    }

    /**
     * Sets the light location on the last defined vertex.
     *
     * @param u the u position in the light
     * @param v the v position in the light
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun light(u: Int, v: Int) = apply {
        lightInternal(u, v)
    }

    /**
     * Sets the line width when rendering [DrawMode.LINES]
     *
     * @param width the width of the line
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun lineWidth(width: Float) = apply {
        lineWidthInternal(width)
    }
    @JvmStatic
    fun resetLineWidth() = apply {
        lineWidth(1f)
    }

    @JvmStatic
    fun enableCull() = apply {
        PipelineBuilder.enableCull()
    }

    @JvmStatic
    fun disableCull() = apply {
        PipelineBuilder.disableCull()
    }

    @JvmStatic
    fun enableLighting() = apply { }

    @JvmStatic
    fun disableLighting() = apply { }

    @JvmStatic
    fun enableDepth() = apply {
        PipelineBuilder.enableDepth()
    }

    @JvmStatic
    fun disableDepth() = apply {
        PipelineBuilder.disableDepth()
    }

    @JvmStatic
    //#if MC<=12111
    //$$fun getDepthTestFunctionFromInt(value: Int): DepthTestFunction {
    //$$    return when (value) {
    //$$        0x201 -> DepthTestFunction.LESS_DEPTH_TEST // GL_LESS
    //$$        0x202 -> DepthTestFunction.EQUAL_DEPTH_TEST // GL_EQUAL
    //$$        0x203 -> DepthTestFunction.LEQUAL_DEPTH_TEST // GL_LEQUAL
    //$$        0x204 -> DepthTestFunction.GREATER_DEPTH_TEST // GL_GREATER
    //$$        0x207 -> DepthTestFunction.NO_DEPTH_TEST // GL_ALWAYS
    //#else
    fun getDepthTestFunctionFromInt(value: Int): DepthStencilState {
        return when (value) {
            0x201 -> DepthStencilState(CompareOp.LESS_THAN, true) // GL_LESS
            0x202 -> DepthStencilState(CompareOp.EQUAL, true) // GL_EQUAL
            0x203 -> DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true) // GL_LEQUAL
            0x204 -> DepthStencilState(CompareOp.GREATER_THAN, true) // GL_GREATER
            0x207 -> DepthStencilState(CompareOp.ALWAYS_PASS, false) // GL_ALWAYS
            //#endif
            else -> throw IllegalArgumentException("Invalid depth test function value: $value")
        }
    }

    @JvmStatic
    //#if MC<=12111
    //$$fun depthFunc(function: DepthTestFunction) = apply {
    //#else
    fun depthFunc(function: DepthStencilState) = apply {
        //#endif
        PipelineBuilder.setDepthTestFunction(function)
    }

    @JvmStatic
    fun depthFunc(depthFuncInt: Int) = apply {
        depthFunc(getDepthTestFunctionFromInt(depthFuncInt))
    }

    @JvmStatic
    fun depthMask(mask: Boolean) = apply { }

    @JvmStatic
    fun enableTexture2D() = apply { }
    @JvmStatic
    fun disableTexture2D() = apply { }

    @JvmStatic
    fun enableLineSmooth() = apply { }
    @JvmStatic
    fun disableLineSmooth() = apply { }

    @JvmStatic
    fun enableBlend() = apply {
        PipelineBuilder.enableBlend()
    }
    @JvmStatic
    fun disableBlend() = apply {
        PipelineBuilder.disableBlend()
    }

    @JvmStatic
    fun blendFunc(srcFactor: Int, dstFactor: Int) = apply {
        //#if MC<26.2
        //$$blendFunc(getSourceFactorFromInt(srcFactor), getDestFactorFromInt(dstFactor))
        //#else
        blendFunc(getBlendFactorFromInt(srcFactor), getBlendFactorFromInt(dstFactor))
        //#endif
    }

    @JvmStatic
    fun shadeModel(model: Int) = apply { }

    @JvmStatic
    fun blendFunc(function: BlendFunction) = apply {
        PipelineBuilder.setBlendFunction(function)
    }
    @JvmStatic
    //#if MC<26.2
    //$$fun blendFunc(srcFactor: SourceFactor, dstFactor: DestFactor) = apply {
    //#else
    fun blendFunc(srcFactor: BlendFactor, dstFactor: BlendFactor) = apply {
    //#endif
        PipelineBuilder.setBlendFunction(BlendFunction(srcFactor, dstFactor))
    }

    @JvmStatic
    //#if MC<26.2
    //$$fun getSourceFactorFromInt(value: Int): SourceFactor {
    //$$    return when (value) {
    //$$        0 -> SourceFactor.ZERO
    //$$        1 -> SourceFactor.ONE
    //$$        768 -> SourceFactor.SRC_COLOR
    //$$        769 -> SourceFactor.ONE_MINUS_SRC_COLOR
    //$$        774 -> SourceFactor.DST_COLOR
    //$$        775 -> SourceFactor.ONE_MINUS_DST_COLOR
    //$$        32769 -> SourceFactor.CONSTANT_COLOR
    //$$        32770 -> SourceFactor.ONE_MINUS_CONSTANT_COLOR
    //$$        770 -> SourceFactor.SRC_ALPHA
    //$$        771 -> SourceFactor.ONE_MINUS_SRC_ALPHA
    //$$        772 -> SourceFactor.DST_ALPHA
    //$$        773 -> SourceFactor.ONE_MINUS_DST_ALPHA
    //$$        32771 -> SourceFactor.CONSTANT_ALPHA
    //$$        32772 -> SourceFactor.ONE_MINUS_CONSTANT_ALPHA
    //$$        776 -> SourceFactor.SRC_ALPHA_SATURATE
    //$$        else -> throw IllegalArgumentException("Invalid source factor value: $value")
    //$$    }
    //$$}
    //$$@JvmStatic
    //$$fun getDestFactorFromInt(value: Int): DestFactor {
    //$$    return when (value) {
    //$$        0 -> DestFactor.ZERO
    //$$        1 -> DestFactor.ONE
    //$$        768 -> DestFactor.SRC_COLOR
    //$$        769 -> DestFactor.ONE_MINUS_SRC_COLOR
    //$$        774 -> DestFactor.DST_COLOR
    //$$        775 -> DestFactor.ONE_MINUS_DST_COLOR
    //$$        32769 -> DestFactor.CONSTANT_COLOR
    //$$        32770 -> DestFactor.ONE_MINUS_CONSTANT_COLOR
    //$$        770 -> DestFactor.SRC_ALPHA
    //$$        771 -> DestFactor.ONE_MINUS_SRC_ALPHA
    //$$        772 -> DestFactor.DST_ALPHA
    //$$        773 -> DestFactor.ONE_MINUS_DST_ALPHA
    //$$        32771 -> DestFactor.CONSTANT_ALPHA
    //$$        32772 -> DestFactor.ONE_MINUS_CONSTANT_ALPHA
    //$$        else -> throw IllegalArgumentException("Invalid source factor value: $value")
    //$$    }
    //$$}
    //#else
    fun getBlendFactorFromInt(value: Int): BlendFactor {
        return when (value) {
            0 -> BlendFactor.ZERO
            1 -> BlendFactor.ONE
            768 -> BlendFactor.SRC_COLOR
            769 -> BlendFactor.ONE_MINUS_SRC_COLOR
            774 -> BlendFactor.DST_COLOR
            775 -> BlendFactor.ONE_MINUS_DST_COLOR
            32769 -> BlendFactor.CONSTANT_COLOR
            32770 -> BlendFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> BlendFactor.SRC_ALPHA
            771 -> BlendFactor.ONE_MINUS_SRC_ALPHA
            772 -> BlendFactor.DST_ALPHA
            773 -> BlendFactor.ONE_MINUS_DST_ALPHA
            32771 -> BlendFactor.CONSTANT_ALPHA
            32772 -> BlendFactor.ONE_MINUS_CONSTANT_ALPHA
            776 -> BlendFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid blend factor value: $value")
        }
    }
    //#endif

    @JvmStatic
    fun tryBlendFuncSeparate(
        //#if MC<26.2
        //$$sourceFactor: SourceFactor,
        //$$destFactor: DestFactor,
        //$$sourceFactorAlpha: SourceFactor,
        //$$destFactorAlpha: DestFactor,
        //#else
        sourceFactor: BlendFactor,
        destFactor: BlendFactor,
        sourceFactorAlpha: BlendFactor,
        destFactorAlpha: BlendFactor,
        //#endif
    ) = apply {
        blendFunc(BlendFunction(sourceFactor, destFactor, sourceFactorAlpha, destFactorAlpha))
    }

    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: Int,
        destFactor: Int,
        sourceFactorAlpha: Int,
        destFactorAlpha: Int,
    ) = apply {
        tryBlendFuncSeparate(
            //#if MC<26.2
            //$$getSourceFactorFromInt(sourceFactor),
            //$$getDestFactorFromInt(destFactor),
            //$$getSourceFactorFromInt(sourceFactorAlpha),
            //$$getDestFactorFromInt(destFactorAlpha),
            //#else
            getBlendFactorFromInt(sourceFactor),
            getBlendFactorFromInt(destFactor),
            getBlendFactorFromInt(sourceFactorAlpha),
            getBlendFactorFromInt(destFactorAlpha),
            //#endif
        )
    }

    @JvmStatic
    fun deleteTexture(texture: Image) = apply {
        GL11.glDeleteTextures(texture.getTexture()?.pixels?.pointer?.toInt() ?: 0)
    }

    @JvmStatic
    fun setShaderTexture(texture: GpuTextureView?) = apply {
        PipelineBuilder.setTexture(texture?.texture())
    }

    @JvmStatic
    fun setShaderTexture(textureImage: Image) = apply {
        val gpuTexture = textureImage.getTexture()
        gpuTexture?.let {
            PipelineBuilder.setTexture(gpuTexture.textureView.texture())
        }
    }

    @JvmStatic
    fun pushMatrix(stack: UMatrixStack = matrixStack) = apply {
        matrixPushCounter++
        matrixStackStack.addLast(stack)
        matrixStack = stack
        stack.push()
    }

    @JvmStatic
    fun popMatrix() = apply {
        matrixPushCounter--
        matrixStackStack.removeLast()
        matrixStack.pop()
    }

    @JvmStatic
    @JvmOverloads
    fun translate(x: Float, y: Float, z: Float = 0.0F) = apply {
        matrixStack.translate(x, y, z)
    }
    @JvmStatic
    @JvmOverloads
    fun translate(x: Double, y: Double, z: Double = 0.0) = apply {
        translate(x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Float, scaleY: Float = scaleX, scaleZ: Float = scaleX) = apply {
        matrixStack.scale(scaleX, scaleY, scaleZ)
    }
    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Double, scaleY: Double = scaleX, scaleZ: Double = scaleX) = apply {
        scale(scaleX.toFloat(), scaleY.toFloat(), scaleZ.toFloat())
    }

    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Float, x: Float = 0f, y: Float = 0f, z: Float = 1f) = apply {
        matrixStack.rotate(angle, x, y, z)
    }
    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Double, x: Double = 0.0, y: Double = 0.0, z: Double = 1.0) = apply {
        rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmStatic
    fun multiply(quaternion: Quaternionf) = apply {
        matrixStack.multiply(quaternion)
    }

    @JvmStatic
    fun enableScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Double, scissorY: Double, scissorWidth: Double, scissorHeight: Double
    ) = apply {
        enableScissor(drawContext, scissorX.toInt(), scissorY.toInt(), scissorWidth.toInt(), scissorHeight.toInt())
    }

    @JvmStatic
    fun enableScaledScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Double, scissorY: Double, scissorWidth: Double, scissorHeight: Double
    ) = apply {
        enableScaledScissor(drawContext, scissorX.toInt(), scissorY.toInt(), scissorWidth.toInt(), scissorHeight.toInt())
    }

    @JvmStatic
    fun enableScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Float, scissorY: Float, scissorWidth: Float, scissorHeight: Float
    ) = apply {
        enableScissor(drawContext, scissorX.toInt(), scissorY.toInt(), scissorWidth.toInt(), scissorHeight.toInt())
    }

    @JvmStatic
    fun enableScaledScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Float, scissorY: Float, scissorWidth: Float, scissorHeight: Float
    ) = apply {
        enableScaledScissor(drawContext, scissorX.toInt(), scissorY.toInt(), scissorWidth.toInt(), scissorHeight.toInt())
    }

    @JvmStatic
    fun enableScaledScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Int, scissorY: Int, scissorWidth: Int, scissorHeight: Int
    ) = apply {
        val screenHeight = screen.getHeight()
        val screenScale = 2
        enableScissor(drawContext, scissorX * screenScale, (screenHeight - (scissorY + scissorHeight)) * screenScale, scissorWidth * screenScale, scissorHeight * screenScale)
    }

    @JvmStatic
    fun enableScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        scissorX: Int, scissorY: Int, scissorWidth: Int, scissorHeight: Int
    ) = apply {
        drawContext.scissorStack.push(ScreenRectangle(scissorX / 2, scissorY / 2, scissorWidth / 2, scissorHeight / 2))
    }

    @JvmStatic
    fun disableScissor(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
    ) = apply {
        drawContext.scissorStack.pop()
    }

    @JvmStatic
    fun colorizeRGBA(color: Long) = apply {
        val (r, g, b, a) = RGBAColor.fromLongRGBA(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    fun colorizeARGB(color: Long) = apply {
        val (a, r, g, b) = ARGBColor.fromLongARGB(color)
        colorize_255(r, g, b, a)
    }

    @JvmStatic
    @JvmOverloads
    fun colorize_01(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        val red = (r * 255f).coerceIn(0f, 255f).toInt()
        val green = (g * 255f).coerceIn(0f, 255f).toInt()
        val blue = (b * 255f).coerceIn(0f, 255f).toInt()
        val alpha = (a * 255f).coerceIn(0f, 255f).toInt()
        colorized = RGBAColor(red, green, blue, alpha).getLong()
        vertexColor = Color(red, green, blue, alpha)
    }

    @JvmStatic
    fun colorize_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        colorize_01(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f,
        )
    }

    @JvmStatic
    @JvmOverloads
    fun color_01(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        colorInternal(r, g, b, a)
    }

    @JvmStatic
    @JvmOverloads
    fun color_255(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        color_01(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    @JvmStatic
    fun color(color: Color) = apply {
        color_255(color.red, color.green, color.blue, color.alpha)
    }

    @JvmStatic
    fun colorRGBA(color: Long) = apply {
        val (r, g, b, a) = RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
        color_255(r, g, b, a)
    }

    @JvmStatic
    fun colorARGB(color: Long) = apply {
        val (a, r, g, b) = RGBAColor.fromLongRGBA(color).getIntComponentsRGBA()
        color_255(r, g, b, a)
    }

    @JvmStatic
    fun resetColor() = apply {
        colorize_01(1f, 1f, 1f, 1f)
    }

    // Color Compat
    @JvmStatic
    fun fixAlpha(color: Long): Long {
        val alpha = color ushr 24 and 255
        return if (alpha < 10) {
            (color and 0xFF_FF_FF) or 0xA_FF_FF_FF
        } else {
            color
        }
    }
    @JvmStatic
    @JvmOverloads
    fun getColor(r: Int, g: Int, b: Int, a: Int = 255): Long {
        return RGBAColor(r, g, b, a).getLong()
    }
    @JvmStatic
    @JvmOverloads
    fun getColor(r: Float, g: Float, b: Float, a: Float = 255f): Long = getColor(
        r.toInt(), g.toInt(), b.toInt(), a.toInt()
    )

    @JvmStatic
    @JvmOverloads
    fun getColor0_1(r: Float, g: Float, b: Float, a: Float = 1f): Long {
        val ri = (r.coerceIn(0f, 1f) * 255).toInt()
        val gi = (g.coerceIn(0f, 1f) * 255).toInt()
        val bi = (b.coerceIn(0f, 1f) * 255).toInt()
        val ai = (a.coerceIn(0f, 1f) * 255).toInt()

        val colorInt = ((ai and 0xFF) shl 24) or
                ((ri and 0xFF) shl 16) or
                ((gi and 0xFF) shl 8) or
                (bi and 0xFF)

        return colorInt.toLong() and 0xFFFFFFFFL
    }

    @JvmStatic
    fun getColorRGBA(color: Long): FloatArray {
        val intColor = color.toInt()
        val r = ((intColor shr 24) and 0xFF).toFloat() / 255f
        val g = ((intColor shr 16) and 0xFF).toFloat() / 255f
        val b = ((intColor shr 8) and 0xFF).toFloat() / 255f
        val a = (intColor and 0xFF).toFloat() / 255f

        return floatArrayOf(
            r.coerceIn(0f, 1f),
            g.coerceIn(0f, 1f),
            b.coerceIn(0f, 1f),
            a.coerceIn(0f, 1f),
        )
    }

    @JvmStatic
    fun getARGBColorFromRGBAColor(color: Long): Long {
        val (r, g, b, a) = getColorRGBA(color)
        return getColor0_1(a, r, g, b)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbowColors(step: Float, speed: Float = 1f): IntArray {
        val red = ((sin(step / speed) + 0.75) * 170).toInt()
        val green = ((sin(step / speed + 2 * PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(step / speed + 4 * PI / 3) + 0.75) * 170).toInt()
        return intArrayOf(red, green, blue)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbow(step: Float, speed: Float = 1f): Long {
        val (r, g, b) = getRainbowColors(step, speed)
        return RGBAColor(r, g, b).getLong()
    }

    fun Matrix3x2f.toMatrix4f(): Matrix4f = Matrix4f(
        m00(), m01(), 0f, 0f,
        m10(), m11(), 0f, 0f,
        0f, 0f, 1f, 0f,
        m20(), m21(), 0f, 1f
    )

    @JvmStatic
    fun getGUIMatrix(matrix: Matrix3x2f): Matrix4f {
        val newMatrix = matrix.toMatrix4f()
        return newMatrix
    }

    @JvmStatic
    fun calculateCenter(
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
    ) : Any {
        val cx = (x1 + x2) / 2
        val cy =  if (y1 > y2) y2 else y1
        val cz = (z1 + z2) / 2

        val wx = abs(x2 - x1)
        val h = abs(y2 - y1)
        val wz = abs(z2 - z1)

        return object {
            val cx = cx
            val cy = cy
            val cz = cz
            val wx = wx
            val h = h
            val wz = wz
        }
    }

    /**
     * Gets a fixed render position from x, y, and z inputs adjusted with partial ticks
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the Vec3f position to render at
     */
    @JvmStatic
    fun getRenderPos(x: Float, y: Float, z: Float): Vec3f {
        return Vec3f(
            x - CTPlayer.getRenderX().toFloat(),
            y - CTPlayer.getRenderY().toFloat(),
            z - CTPlayer.getRenderZ().toFloat(),
        )
    }

    data class WorldPositionVertex(
        val x: Float,
        val y: Float,
        val z: Float,
        val normal: Vector3f?,
        val lineWidth: Float,
    )

    data class TextLines(
        val lines: List<Component>,
        val width: Float,
        val height: Float
    )

//    @JvmStatic
//    internal fun splitText(text: String): TextLines {
//        val lines = ChatLib.addColor(text).split(NEWLINE_REGEX)
//        return TextLines(
//            lines,
//            lines.maxOf { getFontRenderer().width(it) }.toFloat(),
//            (getFontRenderer().lineHeight * lines.size + (lines.size - 1)).toFloat(),
//        )
//    }
    @JvmStatic
    fun splitText(text: Component, maxWidth: Int): TextLines {
        val textRenderer = getTextRenderer()
        val wrappedLines = textRenderer.splitter.splitLines(text, maxWidth, Style.EMPTY)

        val textLines = wrappedLines.map { visitable ->
            val builder = Component.empty()
            visitable.visit({ style, content ->
                builder.append(Component.literal(content).setStyle(style))
                Optional.empty<Unit>()
            }, Style.EMPTY)
            builder
        }

        val width = textLines.maxOfOrNull { textRenderer.width(it).toFloat() } ?: 0f
        val height = (textRenderer.lineHeight * textLines.size + (textLines.size - 1)).toFloat()
        return TextLines(textLines, width, height)
    }

    fun blendColorsRGBA(color1: Long, color2: Long): RGBAColor {
        return blendColorsRGBA(
            RGBAColor.fromLongRGBA(color1),
            RGBAColor.fromLongRGBA(color2)
        )
    }
    fun blendColorsRGBA(color1: RenderColor, color2: RenderColor): RGBAColor {
        val (r1, g1, b1, a1) = color1.getIntComponentsRGBA()
        val (r2, g2, b2, a2) = color2.getIntComponentsRGBA()
        return RGBAColor(
            (r1 + r2) / 2,
            (g1 + g2) / 2,
            (b1 + b2) / 2,
            (a1 + a2) / 2
        )
    }

    @JvmStatic
    fun isVisible(targetX: Float, targetY: Float, targetZ: Float): Boolean {
        return isVisible(targetX.toDouble(), targetY.toDouble(), targetZ.toDouble())
    }
    @JvmStatic
    fun isVisible(targetX: Double, targetY: Double, targetZ: Double): Boolean {
        val world = Client.getMinecraft().level ?: return false
        val cameraEntity = getCamera().entity() ?: return false
        val result = world.clip(
            ClipContext(
                getCameraPos(),
                Vec3(targetX, targetY, targetZ),
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                cameraEntity,
            )
        )
        return result.type == HitResult.Type.MISS
    }

    val defaultARGBColor = ARGBColor(255, 255, 255, 255)
    val defaultRGBAColor = RGBAColor(255, 255, 255, 255)
    abstract class RenderColor {
        abstract val r: Int
        abstract val g: Int
        abstract val b: Int
        abstract val a: Int

        operator fun component1() = r
        operator fun component2() = g
        operator fun component3() = b
        operator fun component4() = a

        abstract fun getLong(): Long
        fun Long.toColorInt(): Int = (this and 0xFFFFFFFFL).toInt()

        fun getIntRGBA(): Int = getLongRGBA().toColorInt()
        fun getIntComponentsRGBA(): IntArray = intArrayOf(r, g, b, a)
        fun getLongRGBA(): Long {
            return (
                    (r.coerceIn(0, 255) shl 24) or
                            (g.coerceIn(0, 255) shl 16) or
                            (b.coerceIn(0, 255) shl 8) or
                            a.coerceIn(0, 255)
                    ).toLong() and 0xFFFFFFFFL
        }
        fun getRGBA(): FloatArray = floatArrayOf(
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
            a.coerceIn(0, 255) / 255f,
        )

        fun getIntARGB(): Int = getLongARGB().toColorInt()
        fun getIntComponentsARGB(): IntArray = intArrayOf(a, r, g, b)
        fun getLongARGB(): Long {
            return (
                    (a.coerceIn(0, 255) shl 24) or
                            (r.coerceIn(0, 255) shl 16) or
                            (g.coerceIn(0, 255) shl 8) or
                            b.coerceIn(0, 255)
                    ).toLong() and 0xFFFFFFFFL
        }
        fun getARGB(): FloatArray = floatArrayOf(
            a.coerceIn(0, 255) / 255f,
            r.coerceIn(0, 255) / 255f,
            g.coerceIn(0, 255) / 255f,
            b.coerceIn(0, 255) / 255f,
        )

        fun getHex(): String {
            val hex = StringBuilder("")
            hex.append(r.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(g.coerceIn(0, 255).toString(16).padStart(2, '0'))
            hex.append(b.coerceIn(0, 255).toString(16).padStart(2, '0'))
            return hex.toString().uppercase()
        }

        override fun toString(): String = "Color(r=$r, g=$g, b=$b, a=$a)"

        companion object {
            internal fun parseHex(hex: String): IntArray {
                val clean = hex.removePrefix("#")
                val len = if (clean.length == 8) 8 else 6
                val padded = clean.padStart(len, '0')
                val r = Integer.parseInt(padded.substring(0, 2), 16)
                val g = Integer.parseInt(padded.substring(2, 4), 16)
                val b = Integer.parseInt(padded.substring(4, 6), 16)
                val a = if (len == 8) Integer.parseInt(padded.substring(6, 8), 16) else 255
                return intArrayOf(r, g, b, a)
            }

            internal fun parseLongRGBA(color: Long): IntArray {
                val intColor = color.toInt()
                val r = (intColor shr 24) and 0xFF
                val g = (intColor shr 16) and 0xFF
                val b = (intColor shr 8) and 0xFF
                val a = intColor and 0xFF
                return intArrayOf(r, g, b, a)
            }
            internal fun parseLongARGB(color: Long): IntArray {
                val (r, g, b, a) = parseLongRGBA(color)
                return intArrayOf(a, r, g, b)
            }
        }
    }
    fun Long.toIntRGBA(): Int = RGBAColor.fromLongRGBA(this).getIntRGBA()
    fun Long.toIntARGB(): Int = RGBAColor.fromLongRGBA(this).getIntARGB()

    class ARGBColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongARGB()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): ARGBColor {
                val (r, g, b, a) = parseHex(hex)
                return ARGBColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return ARGBColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): ARGBColor {
                val (r, g, b, a) = parseLongARGB(color)
                return ARGBColor(r, g, b, a)
            }
            @JvmStatic
            fun fromIntArray(arr: IntArray): ARGBColor {
                val a = if (arr.size >= 1) arr[0] else 255
                val r = if (arr.size >= 2) arr[1] else 255
                val g = if (arr.size >= 3) arr[2] else 255
                val b = if (arr.size >= 4) arr[3] else 255
                return ARGBColor(a, r, g, b)
            }
        }
    }

    class RGBAColor(
        override val r: Int,
        override val g: Int,
        override val b: Int,
        override val a: Int = 255
    ) : RenderColor() {
        override fun getLong(): Long = getLongRGBA()

        companion object {
            @JvmStatic
            fun fromHex(hex: String): RGBAColor {
                val (r, g, b, a) = parseHex(hex)
                return RGBAColor(r, g, b, a)
            }

            @JvmStatic
            fun fromLongRGBA(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongRGBA(color)
                return RGBAColor(r, g, b, a)
            }
            @JvmStatic
            fun fromLongARGB(color: Long): RGBAColor {
                val (r, g, b, a) = parseLongARGB(color)
                return RGBAColor(r, g, b, a)
            }
            @JvmStatic
            fun fromIntArray(arr: IntArray): RGBAColor {
                val r = if (arr.size >= 1) arr[0] else 255
                val g = if (arr.size >= 2) arr[1] else 255
                val b = if (arr.size >= 3) arr[2] else 255
                val a = if (arr.size >= 4) arr[3] else 255
                return RGBAColor(r, g, b, a)
            }
        }
    }

    class TrigCache(segments: Int) {
        val cosTheta = FloatArray(segments * 2 + 1)
        val sinTheta = FloatArray(segments * 2 + 1)
        val cosPhi = FloatArray(segments + 1)
        val sinPhi = FloatArray(segments + 1)

        init {
            val thetaStep = 2.0 * Math.PI / (segments * 2)
            for (i in 0..(segments * 2)) {
                val angle = thetaStep * i
                cosTheta[i] = cos(angle).toFloat()
                sinTheta[i] = sin(angle).toFloat()
            }

            val phiStep = Math.PI / segments
            for (i in 0..segments) {
                val angle = phiStep * i
                cosPhi[i] = cos(angle).toFloat()
                sinPhi[i] = sin(angle).toFloat()
            }
        }
    }

    private val trigCaches = mutableMapOf<Int, TrigCache>()
    fun getTrigCache(segments: Int): TrigCache {
        return trigCaches.getOrPut(segments) { TrigCache(segments) }
    }

    val tempNormal = Vector3f()
    fun Vector3f.setAndNormalize(x: Float, y: Float, z: Float): Vector3f {
        return this.set(x, y, z).normalize()
    }
    fun Vector3f.setAndNormalize(vec: Vector3f): Vector3f {
        return this.set(vec).normalize()
    }
    fun Vector3f.setAndNormalize(from: Vector3f, to: Vector3f): Vector3f {
        return this.set(to).sub(from).normalize()
    }

    enum class FlattenRoundedRectCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        ;
    }
    val TOP_FLAT_CORNERS = mutableListOf(
        FlattenRoundedRectCorner.TOP_LEFT,
        FlattenRoundedRectCorner.TOP_RIGHT,
    )
    val BOTTOM_FLAT_CORNERS = mutableListOf(
        FlattenRoundedRectCorner.BOTTOM_LEFT,
        FlattenRoundedRectCorner.BOTTOM_RIGHT,
    )
    val LEFT_FLAT_CORNERS = mutableListOf(
        FlattenRoundedRectCorner.TOP_LEFT,
        FlattenRoundedRectCorner.BOTTOM_LEFT,
    )
    val RIGHT_FLAT_CORNERS = mutableListOf(
        FlattenRoundedRectCorner.TOP_RIGHT,
        FlattenRoundedRectCorner.BOTTOM_RIGHT,
    )
    val ALL_FLAT_CORNERS = mutableListOf(
        FlattenRoundedRectCorner.TOP_LEFT,
        FlattenRoundedRectCorner.TOP_RIGHT,
        FlattenRoundedRectCorner.BOTTOM_LEFT,
        FlattenRoundedRectCorner.BOTTOM_RIGHT,
    )

    fun getGradientColors(gradientDirection: GradientDirection, startColor: Long, endColor: Long): GradientColors {
        val startRGBA = RGBAColor.fromLongRGBA(startColor).getLong()
        val endRGBA = RGBAColor.fromLongRGBA(endColor).getLong()
        val blendedRGBA = blendColorsRGBA(startRGBA, endRGBA).getLong()
        return when (gradientDirection) {
            GradientDirection.TOP_TO_BOTTOM -> GradientColors(startRGBA, startRGBA, endRGBA, endRGBA)
            GradientDirection.BOTTOM_TO_TOP -> GradientColors(endRGBA, endRGBA, startRGBA, startRGBA)
            GradientDirection.LEFT_TO_RIGHT -> GradientColors(startRGBA, endRGBA, startRGBA, endRGBA)
            GradientDirection.RIGHT_TO_LEFT -> GradientColors(endRGBA, startRGBA, endRGBA, startRGBA)
            GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> GradientColors(
                topLeft = startRGBA,
                topRight = blendedRGBA,
                bottomLeft = blendedRGBA,
                bottomRight = endRGBA,
            )
            GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> GradientColors(
                topLeft = blendedRGBA,
                topRight = startRGBA,
                bottomLeft = endRGBA,
                bottomRight = blendedRGBA,
            )
            GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> GradientColors(
                topLeft = blendedRGBA,
                topRight = endRGBA,
                bottomLeft = startRGBA,
                bottomRight = blendedRGBA,
            )
            GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> GradientColors(
                topLeft = endRGBA,
                topRight = blendedRGBA,
                bottomLeft = blendedRGBA,
                bottomRight = startRGBA,
            )
        }
    }
    enum class GradientDirection {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_LEFT_TO_BOTTOM_RIGHT,
        TOP_RIGHT_TO_BOTTOM_LEFT,
        BOTTOM_LEFT_TO_TOP_RIGHT,
        BOTTOM_RIGHT_TO_TOP_LEFT,
		;
    }
    data class GradientColors(
        val topLeft: Long,
        val topRight: Long,
        val bottomLeft: Long,
        val bottomRight: Long,
    )

    class ScreenWrapper {
        fun getWidth(): Int = Client.getMinecraft().window.guiScaledWidth
        fun getHeight(): Int = Client.getMinecraft().window.guiScaledHeight
        fun getFullWidth(): Int = Client.getMinecraft().window.width
        fun getFullHeight(): Int = Client.getMinecraft().window.height
        fun getScale(): Double = Client.getMinecraft().window.guiScale.toDouble()
    }
}
