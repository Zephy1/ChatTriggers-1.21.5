package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat

enum class VertexFormat(private val mcValue: VertexFormat) {
    POSITION_COLOR_LINE_WIDTH(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH),
    POSITION_COLOR_NORMAL_LINE_WIDTH(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH),
    POSITION_COLOR_TEXTURE_LIGHT_NORMAL(DefaultVertexFormat.BLOCK),
    //#if MC<=12111
    //$$POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(DefaultVertexFormat.NEW_ENTITY),
    //#else
    POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(DefaultVertexFormat.ENTITY),
    //#endif
    POSITION_TEXTURE_COLOR_LIGHT(DefaultVertexFormat.PARTICLE),
    POSITION(DefaultVertexFormat.POSITION),
    POSITION_COLOR(DefaultVertexFormat.POSITION_COLOR),
    POSITION_COLOR_NORMAL(DefaultVertexFormat.POSITION_COLOR_NORMAL),
    POSITION_COLOR_LIGHT(DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
    POSITION_TEXTURE(DefaultVertexFormat.POSITION_TEX),
    POSITION_TEXTURE_COLOR(DefaultVertexFormat.POSITION_TEX_COLOR),
    POSITION_COLOR_TEXTURE_LIGHT(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
    POSITION_TEXTURE_LIGHT_COLOR(DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR),
    POSITION_TEXTURE_COLOR_NORMAL(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL),
    ;

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(ucValue: VertexFormat) = entries.first { it.mcValue == ucValue }
    }
}
