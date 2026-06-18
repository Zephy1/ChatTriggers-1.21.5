package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier

import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import com.mojang.blaze3d.textures.GpuTexture

//#if MC<=12111
//$$import com.mojang.blaze3d.platform.DepthTestFunction
//#else
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.DepthStencilState
import java.util.Optional
//#endif

//#if MC>=26.2
import com.mojang.blaze3d.GpuFormat
//#endif

object PipelineBuilder {
    private val layerList = mutableMapOf<String, RenderType>()
    private val pipelineList = mutableMapOf<String, RenderPipeline>()
    private var cull: Boolean? = null

    //#if MC<=12111
    //$$private var depthTestFunction: DepthTestFunction? = null
    //$$private var blendFunction: BlendFunction? = null
    //#else
    private var depthTestFunction: Optional<DepthStencilState> = Optional.empty()
    private var blendFunction: Optional<BlendFunction> = Optional.empty()
    //#endif

    private var layering: LayeringTransform? = null
    private var texture: GpuTexture? = null

    private var textureIdentifier: Identifier? = null
    private var drawMode = DrawMode.QUADS
    private var vertexFormat = VertexFormat.POSITION_COLOR
    private var snippet = RenderSnippet.POSITION_COLOR_SNIPPET
    private var location: String? = null
    private var bufferSize: Int? = null

    @JvmStatic
    @JvmOverloads
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        this.drawMode = drawMode
        this.vertexFormat = vertexFormat
        this.snippet = snippet
    }

    @JvmStatic
    fun enableBlend() = apply {
        setBlendFunction(BlendFunction.TRANSLUCENT)
    }

    @JvmStatic
    fun disableBlend() = apply {
        //#if MC<=12111
        //$$blendFunction = null
        //#else
        blendFunction = Optional.empty()
        //#endif
    }

    @JvmStatic
    fun enableCull() = apply {
        cull = true
    }

    @JvmStatic
    fun disableCull() = apply {
        cull = false
    }

    @JvmStatic
    fun enableDepth() = apply {
        RenderUtils.depthFunc(RenderUtils.getDepthTestFunctionFromInt(0x203)) // LEQUAL_DEPTH_TEST, LESS_THAN_OR_EQUAL
    }

    @JvmStatic
    fun disableDepth() = apply {
        RenderUtils.depthFunc(RenderUtils.getDepthTestFunctionFromInt(0x207)) // NO_DEPTH_TEST, ALWAYS_PASS
    }

    @JvmStatic
    fun setLocation(newValue: String?) = apply {
        location = newValue
    }

    @JvmStatic
    fun setLayering(newValue: LayeringTransform?) = apply {
        layering = newValue
    }

    @JvmStatic
    fun setLineWidth(newValue: Float?) = apply { }

    @JvmStatic
    fun setTexture(newValue: Identifier?) = apply {
        textureIdentifier = newValue
    }
    @JvmStatic
    fun setTexture(newValue: GpuTexture?) = apply {
        texture = newValue
    }

    @JvmStatic
    //#if MC<=12111
    //$$fun setDepthTestFunction(newValue: DepthTestFunction) = apply {
    //$$    depthTestFunction = newValue
    //#else
    fun setDepthTestFunction(newValue: DepthStencilState?) = apply {
        depthTestFunction = Optional.ofNullable(newValue)
        //#endif
    }

    @JvmStatic
    fun setBlendFunction(newValue: BlendFunction) = apply {
        //#if MC<=12111
        //$$blendFunction = newValue
        //#else
        blendFunction = Optional.of(newValue)
        //#endif
    }

    @JvmStatic
    fun setBufferSize(size: Int) = apply {
        bufferSize = size
    }

    @JvmStatic
    fun build(): RenderPipeline {
        if (pipelineList.containsKey(state())) return pipelineList[state()]!!

        val basePipeline = RenderPipeline
            .builder(snippet.toMC())
            .withLocation("ctjs/custom/pipelines/${location ?: hashCode()}")
            //#if MC<26.2
            //$$.withVertexFormat(vertexFormat.toMC(), drawMode.toMC())
            //#else
            .withVertexBinding(0, vertexFormat.toMC())
            .withPrimitiveTopology(drawMode.toMC())
            //#endif

        //#if MC<=12111
        //$$blendFunction?.let {
        //$$    basePipeline.withBlend(it)
        //$$} ?: basePipeline.withoutBlend()
        //#else
        //#if MC<26.2
        //$$basePipeline.withColorTargetState(ColorTargetState(blendFunction, 15))
        //#else
        basePipeline.withColorTargetState(0, ColorTargetState(blendFunction, GpuFormat.RGBA8_UNORM, 15))
        //#endif
        //#endif

        cull?.let {
            basePipeline.withCull(cull!!)
        }

        //#if MC<=12111
        //$$depthTestFunction?.let {
        //$$    when (it) {
        //$$        DepthTestFunction.NO_DEPTH_TEST -> basePipeline.withDepthWrite(false)
        //$$        else -> basePipeline.withDepthWrite(true)
        //$$    }
        //$$    basePipeline.withDepthTestFunction(it)
        //$$}
        //#else
        basePipeline.withDepthStencilState(depthTestFunction)
        //#endif

        val pipeline = basePipeline.build()
        pipelineList[state()] = pipeline

        return pipeline
    }

    @JvmStatic
    fun layer(): RenderType {
        try {
            if (layerList.containsKey(state())) return layerList[state()]!!

            val layerBuilder = RenderSetup.builder(build())

            if (textureIdentifier != null) {
                layerBuilder.withTexture("ctjs/custom/textures/${location ?: hashCode()}", textureIdentifier!!)
            }

            if (layering != null) {
                layerBuilder.setLayeringTransform(layering!!)
            }

            //#if MC>=12111
            //#if MC<=12111
            //$$if (blendFunction != null) {
            //#else
            if (blendFunction.isPresent) {
                //#endif
                layerBuilder.sortOnUpload()
            }
            //#endif

            val layer = createRenderLayer(
                "ctjs/custom/layers/${location ?: hashCode()}",
                layerBuilder.createRenderSetup(),
            )

            layerList[state()] = layer
            return layer
        } finally {
            reset()
        }
    }

    //#if MC>=12111
    private fun createRenderLayer(name: String, renderSetup: RenderSetup) =
        RenderType::class.java.declaredMethods.first {
            it.returnType == RenderType::class.java && it.parameterTypes.contentEquals(arrayOf(
                String::class.java,
                RenderSetup::class.java,
            ))
        }.apply { isAccessible = true }.invoke(null, name, renderSetup) as RenderType
    //#endif

    @JvmStatic
    private fun reset() {
        cull = null
        //#if MC<=12111
        //$$depthTestFunction = null
        //$$blendFunction = null
        //#else
        depthTestFunction = Optional.empty()
        blendFunction = Optional.empty()
        //#endif
        layering = null
        textureIdentifier = null
        drawMode = DrawMode.QUADS
        vertexFormat = VertexFormat.POSITION_COLOR
        snippet = RenderSnippet.POSITION_COLOR_SNIPPET
        location = null
        bufferSize = null
        texture = null
    }

    @JvmStatic
    fun state(): String {
        return (
            "PipelineBuilder[" +
                "location=${location}, " +
                "cull=${cull}, " +
                "depth=${depthTestFunction}, " +
                "blend=${blendFunction}, " +
                "layering=${layering}, " +
                "drawMode=${drawMode.name}, " +
                "vertexFormat=${vertexFormat.name}, " +
                "snippet=${snippet.name}, " +
                "textureIdentifier=${textureIdentifier}, " +
                "bufferSize=${bufferSize}, " +
                "texture=${texture}" +
                "]"
            )
    }
}
