package com.chattriggers.ctjs.api.render

//#if MC<26.2
//$$import com.mojang.blaze3d.vertex.VertexFormat
//$$enum class DrawMode(private val mcValue: VertexFormat.Mode) {
//$$LINES(VertexFormat.Mode.LINES),
//$$LINE_STRIP(VertexFormat.Mode.DEBUG_LINE_STRIP),
//$$TRIANGLES(VertexFormat.Mode.TRIANGLES),
//$$TRIANGLE_STRIP(VertexFormat.Mode.TRIANGLE_STRIP),
//$$TRIANGLE_FAN(VertexFormat.Mode.TRIANGLE_FAN),
//$$QUADS(VertexFormat.Mode.QUADS),
//#else
import com.mojang.blaze3d.PrimitiveTopology
enum class DrawMode(private val mcValue: PrimitiveTopology) {
    LINES(PrimitiveTopology.LINES),
    LINE_STRIP(PrimitiveTopology.DEBUG_LINE_STRIP),
    TRIANGLES(PrimitiveTopology.TRIANGLES),
    TRIANGLE_STRIP(PrimitiveTopology.TRIANGLE_STRIP),
    TRIANGLE_FAN(PrimitiveTopology.TRIANGLE_FAN),
    QUADS(PrimitiveTopology.QUADS),
//#endif
    ;

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(
            //#if MC<26.2
            //$$mcValue: VertexFormat.Mode
            //#else
            mcValue: PrimitiveTopology
            //#endif
        ) = entries.first { it.mcValue == mcValue }
    }
}
