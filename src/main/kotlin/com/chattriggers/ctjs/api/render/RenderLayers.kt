package com.chattriggers.ctjs.api.render

import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier

object RenderLayers {
    @JvmStatic
    fun getRenderLayer(
        drawMode: DrawMode,
        vertexFormat: VertexFormat? = null,
    ): RenderType? {
        if (vertexFormat != null) {
            return getRenderLayerFunction_DrawModeVertexFormat(drawMode)?.invoke(drawMode, vertexFormat)
        }
        return getRenderLayerFunction_DrawMode(drawMode)?.invoke(drawMode)
    }

    private fun getRenderLayerFunction_DrawModeVertexFormat(drawMode: DrawMode): ((DrawMode, VertexFormat) -> RenderType)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    private fun getRenderLayerFunction_DrawMode(drawMode: DrawMode): ((DrawMode) -> RenderType)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    private fun getRenderLayerFunction(drawMode: DrawMode): (() -> RenderType)? {
        return when (drawMode) {
            DrawMode.LINES -> ::LINES
            DrawMode.LINE_STRIP -> ::LINE_STRIP
            DrawMode.TRIANGLES -> ::TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::TRIANGLE_STRIP
            DrawMode.TRIANGLE_FAN -> ::TRIANGLE_FAN
            DrawMode.QUADS -> ::QUADS
        }
    }

    @JvmStatic
    fun LINES(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderType {
        return RenderPipelines
            .LINES(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINES_ESP(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderType {
        return RenderPipelines
            .LINES_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINE_STRIP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderType {
        return RenderPipelines
            .LINE_STRIP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun LINE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH,
        snippet: RenderSnippet = RenderSnippet.LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderType {
        return RenderPipelines
            .LINE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun TRIANGLES(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLES(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLES_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLES_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_STRIP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLE_STRIP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_FAN(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLE_FAN(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun TRIANGLE_FAN_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_FAN,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .TRIANGLE_FAN_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .QUADS(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderType {
        return RenderPipelines
            .QUADS_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    fun TEXTURED_QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderType {
        return RenderPipelines
            .TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }

    fun TEXTURED_QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderType {
        return RenderPipelines
            .TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }
}
