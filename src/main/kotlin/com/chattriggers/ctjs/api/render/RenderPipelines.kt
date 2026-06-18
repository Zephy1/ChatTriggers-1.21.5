package com.chattriggers.ctjs.api.render

import net.minecraft.client.renderer.rendertype.LayeringTransform

object RenderPipelines {
    private fun createPipelineBuilder(
        location: String? = null,
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
    ): PipelineBuilder {
        return PipelineBuilder
            .begin(drawMode, vertexFormat, snippet)
            .setLocation(location)
            .setLayering(LayeringTransform.NO_LAYERING)
            .disableCull()
            .enableBlend()
    }

    private fun createESPPipelineBuilder(
        location: String? = null,
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
    ): PipelineBuilder {
        return PipelineBuilder
            .begin(drawMode, vertexFormat, snippet)
            .setLocation(location)
            .setLayering(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .disableCull()
            .disableDepth()
    }

    @JvmStatic
    fun LINES(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("lines", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun LINES_ESP(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("lines_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun LINE_STRIP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("line_strip", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun LINE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("line_strip_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLES(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("triangles", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLES_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("triangles_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLE_STRIP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("triangle_strip", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("triangle_strip_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLE_FAN(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("triangle_fan", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TRIANGLE_FAN_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("triangle_fan_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("quads", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("quads_esp", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TEXTURED_QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
    ): PipelineBuilder = createPipelineBuilder("textured_quads", drawMode, vertexFormat, snippet)

    @JvmStatic
    fun TEXTURED_QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
    ): PipelineBuilder = createESPPipelineBuilder("textured_quads_esp", drawMode, vertexFormat, snippet)
}
