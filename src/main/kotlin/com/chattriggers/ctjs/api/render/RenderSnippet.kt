package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.pipeline.RenderPipeline
import net.minecraft.client.renderer.RenderPipelines

enum class RenderSnippet(val mcValue: RenderPipeline.Snippet) {
    TERRAIN_SNIPPET(RenderPipelines.TERRAIN_SNIPPET),
    ENTITY_SNIPPET(RenderPipelines.ENTITY_SNIPPET),
    BEACON_BEAM_SNIPPET(RenderPipelines.BEACON_BEAM_SNIPPET),
    TEXT_SNIPPET(RenderPipelines.TEXT_SNIPPET),
    END_PORTAL_SNIPPET(RenderPipelines.END_PORTAL_SNIPPET),
    CLOUDS_SNIPPET(RenderPipelines.CLOUDS_SNIPPET),
    LINES_SNIPPET(RenderPipelines.LINES_SNIPPET),
    POSITION_COLOR_SNIPPET(RenderPipelines.DEBUG_FILLED_SNIPPET),
    PARTICLE_SNIPPET(RenderPipelines.PARTICLE_SNIPPET),
    WEATHER_SNIPPET(RenderPipelines.WEATHER_SNIPPET),
    GUI_SNIPPET(RenderPipelines.GUI_SNIPPET),
    POSITION_TEX_COLOR_SNIPPET(RenderPipelines.GUI_TEXTURED_SNIPPET),
    OUTLINE_SNIPPET(RenderPipelines.OUTLINE_SNIPPET),
    POST_EFFECT_PROCESSOR_SNIPPET(RenderPipelines.POST_PROCESSING_SNIPPET),
    ;

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(mcValue: RenderPipeline.Snippet) = RenderSnippet.entries.first { it.mcValue == mcValue }
    }
}