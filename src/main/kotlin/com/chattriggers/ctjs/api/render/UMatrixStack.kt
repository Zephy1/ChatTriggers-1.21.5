package com.chattriggers.ctjs.api.render

import java.util.*

//#if MC<11700
//$$import org.lwjgl.opengl.GL11
//$$import org.lwjgl.util.vector.Matrix3f
//$$import org.lwjgl.util.vector.Matrix4f
//$$import org.lwjgl.util.vector.Quaternion
//$$import org.lwjgl.util.vector.Vector3f
//$$import kotlin.math.cos
//$$import kotlin.math.sin
//$$import java.nio.Buffer
//$$import java.nio.FloatBuffer
//$$import net.minecraft.client.renderer.GlStateManager
//#else
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Quaternionf
//#endif

//#if MC<11700
//$$import net.minecraft.client.renderer.GLAllocation
//#else
import com.mojang.blaze3d.systems.RenderSystem
//#endif

//#if MC>=11600
import com.mojang.blaze3d.vertex.PoseStack
//#endif

//#if MC<11400
//$$import kotlin.math.acos
//$$import kotlin.math.sqrt
//#else
import net.minecraft.util.Mth
//#endif

class UMatrixStack private constructor(
    private val stack: Deque<Entry>
) {
    constructor() : this(ArrayDeque<Entry>().apply {
        add(Entry(
            //#if MC<11400
            //$$Matrix4f().apply { setIdentity() },
            //$$Matrix3f().apply { setIdentity() },
            //#else
            Matrix4f().identity(),
            Matrix3f().identity(),
            //#endif
        ))
    })

    //#if MC>=11600
    constructor(mc: PoseStack) : this(mc.last())
    constructor(mc: PoseStack.Pose) : this(ArrayDeque<Entry>().apply {
        add(Entry(Matrix4f(mc.pose()), Matrix3f(mc.normal())))
    })
    fun toMC() = peek().toMCStack()
    //#endif

    //#if MC>=12106
    constructor(mc: org.joml.Matrix3x2f) : this() {
        peek().model.apply {
            m00(mc.m00)
            m01(mc.m01)
            m10(mc.m10)
            m11(mc.m11)
            m30(mc.m20)
            m31(mc.m21)
        }
    }
    fun to3x2Joml(dst: org.joml.Matrix3x2f = org.joml.Matrix3x2f()): org.joml.Matrix3x2f {
        val uc = peek().model
        dst.set(uc.m00(), uc.m01(), uc.m10(), uc.m11(), uc.m30(), uc.m31())
        return dst
    }
    //#endif

    fun translate(x: Double, y: Double, z: Double) = translate(x.toFloat(), y.toFloat(), z.toFloat())
    fun translate(x: Float, y: Float, z: Float) {
        if (x == 0f && y == 0f && z == 0f) return
        //#if MC<11400
        //$$stack.last.run {
        //$$    Matrix4f.translate(Vector3f(x, y, z), model, model)
        //$$}
        //#else
        stack.last.model.translate(x, y, z)
        //#endif
    }

    fun scale(x: Double, y: Double, z: Double) = scale(x.toFloat(), y.toFloat(), z.toFloat())
    fun scale(x: Float, y: Float, z: Float) {
        if (x == 1f && y == 1f && z == 1f) return
        stack.last.run {
            //#if MC<11400
            //$$Matrix4f.scale(Vector3f(x, y, z), model, model)
            //#else
            model.scale(x, y, z)
            //#endif

            if (x == y && y == z) {
                if (x < 0f) {
                    //#if MC<11400
                    //$$ Matrix3f.negate(normal, normal)
                    //#else
                    normal.scale(-1f)
                    //#endif
                }
            } else {
                val ix = 1f / x
                val iy = 1f / y
                val iz = 1f / z
                //#if MC<11400
                //$$val rt = Math.cbrt((ix * iy * iz).toDouble()).toFloat()
                //$$val scale = Matrix3f()
                //$$scale.m00 = rt * ix
                //$$scale.m11 = rt * iy
                //$$scale.m22 = rt * iz
                //$$Matrix3f.mul(normal, scale, normal)
                //#else
                val rt = Mth.fastInvCubeRoot(ix * iy * iz)
                normal.scale(rt * ix, rt * iy, rt * iz)
                //#endif
            }
        }
    }

    @JvmOverloads
    fun rotate(angle: Float, x: Float, y: Float, z: Float, degrees: Boolean = true) {
        if (angle == 0f) return
        //#if MC<11400
        //$$stack.last.run {
            //$$val angleRadians = if (degrees) Math.toRadians(angle.toDouble()).toFloat() else angle
            //$$val axis = Vector3f(x, y, z)
            //$$Matrix4f.rotate(angleRadians, axis, model, model)
            //$$fun makeRotationMatrix(angle: Float, axis: Vector3f) = Matrix3f().apply {
            //$$    val c = cos(angle)
            //$$    val s = sin(angle)
            //$$    val oneMinusC = 1 - c
            //$$    val xx = axis.x * axis.x
            //$$    val xy = axis.x * axis.y
            //$$    val xz = axis.x * axis.z
            //$$    val yy = axis.y * axis.y
            //$$    val yz = axis.y * axis.z
            //$$    val zz = axis.z * axis.z
            //$$    val xs = axis.x * s
            //$$    val ys = axis.y * s
            //$$    val zs = axis.z * s
            //$$    m00 = xx * oneMinusC + c
            //$$    m01 = xy * oneMinusC + zs
            //$$    m02 = xz * oneMinusC - ys
            //$$    m10 = xy * oneMinusC - zs
            //$$    m11 = yy * oneMinusC + c
            //$$    m12 = yz * oneMinusC + xs
            //$$    m20 = xz * oneMinusC + ys
            //$$    m21 = yz * oneMinusC - xs
            //$$    m22 = zz * oneMinusC + c
            //$$}
            //$$Matrix3f.mul(normal, makeRotationMatrix(angleRadians, axis), normal)
        //$$}
        //#else
        val angleRadians = if (degrees) Math.toRadians(angle.toDouble()).toFloat() else angle
        multiply(Quaternionf().rotateAxis(angleRadians, x, y, z))
        //#endif
    }

    //#if MC<11400
    //$$fun multiply(quaternion: Quaternion) {
    //#else
    fun multiply(quaternion: Quaternionf) {
    //#endif
        //#if MC<11400
        //$$val angle = 2 * acos(quaternion.w)
        //$$val s = sqrt(1.0 - quaternion.w * quaternion.w).toFloat()
        //$$if (s < 1e-8) {
        //$$    return
        //$$}
        //$$val sInverse = 1 / s
        //$$val x = quaternion.x * sInverse
        //$$val y = quaternion.y * sInverse
        //$$val z = quaternion.z * sInverse
        //$$rotate(angle, x, y, z, degrees = false)
        //#else
        stack.last.run {
            model.rotate(quaternion)
            normal.rotate(quaternion)
        }
        //#endif
    }

    fun fork() = UMatrixStack(ArrayDeque<Entry>().apply {
        add(stack.last.deepCopy())
    })

    fun push(): Unit = stack.addLast(stack.last.deepCopy())

    fun pop() {
        stack.removeLast()
    }

    fun peek(): Entry = stack.last

    fun isEmpty(): Boolean = stack.size == 1

    fun applyToGlobalState() {
        //#if MC<11700
        //$$stack.last.model.store(MATRIX_BUFFER)
        //$$(MATRIX_BUFFER as Buffer).rewind()
        //$$GL11.glMultMatrix(MATRIX_BUFFER)
        //#else
        RenderSystem.getModelViewStack().mul(stack.last.model)
        //#endif
    }

    fun replaceGlobalState() {
        //#if MC<11700
        //$$GL11.glLoadIdentity()
        //#else
        RenderSystem.getModelViewStack().identity()
        //#endif
        applyToGlobalState()
    }

    fun runWithGlobalState(block: Runnable) = runWithGlobalState { block.run() }
    fun <R> runWithGlobalState(block: () -> R): R = withGlobalStackPushed {
        applyToGlobalState()
        block()
    }

    fun runReplacingGlobalState(block: Runnable) = runReplacingGlobalState { block.run() }
    fun <R> runReplacingGlobalState(block: () -> R): R = withGlobalStackPushed {
        replaceGlobalState()
        block()
    }

    private inline fun <R> withGlobalStackPushed(block: () -> R): R {
        //#if MC<11700
        //$$GlStateManager.pushMatrix()
        //#else
        val mvStack = RenderSystem.getModelViewStack()
        mvStack.pushMatrix()
        //#endif
        return block().also {
            //#if MC<11700
            //$$GlStateManager.popMatrix()
            //#else
            mvStack.popMatrix()
            //#endif
        }
    }

    data class Entry(val model: Matrix4f, val normal: Matrix3f) {
        //#if MC>=11600
        fun toMCStack() = PoseStack().also {
            it.last().pose().mul(model)
            it.last().normal().mul(normal)
        }
        //#endif

        //#if MC<11400
        //$$fun deepCopy() = Entry(Matrix4f.load(model, null), Matrix3f.load(normal, null))
        //#else
        fun deepCopy() = Entry(Matrix4f(model), Matrix3f(normal))
        //#endif

        val modelAsArray: FloatArray
            //#if MC<11400
            //$$get() = with(model) {
            //$$    floatArrayOf(
            //$$        m00, m10, m20, m30,
            //$$        m01, m11, m21, m31,
            //$$        m02, m12, m22, m32,
            //$$        m03, m13, m23, m33,
            //$$    )
            //$$}
            //#else
            get() = FloatArray(16).also { arr ->
                model.get(arr)
            }
            //#endif
    }

    object Compat {
        const val DEPRECATED = """For 1.17 this method requires you pass a UMatrixStack as the first argument.

If you are currently extending this method, you should instead extend the method with the added argument.
Note however for this to be non-breaking, your parent class needs to transition before you do.

If you are calling this method and you cannot guarantee that your target class has been fully updated (such as when
calling an open method on an open class), you should instead call the method with the "Compat" suffix, which will
call both methods, the new and the deprecated one.
If you are sure that your target class has been updated (such as when calling the super method), you should
(for super calls you must!) instead just call the method with the original name and added argument."""

        private val stack = mutableListOf<UMatrixStack>()

        /**
         * To preserve backwards compatibility with old subclasses of UScreen or similar hierarchies,
         * this method allows one to sneak in an artificial matrix stack argument when calling the legacy method
         * which can then later be retrieved via [get] when the base legacy method calls the new one.
         *
         * For an example see [UScreen.onDrawScreenCompat].
         */
        fun <R> runLegacyMethod(matrixStack: UMatrixStack, block: () -> R): R {
            stack.add(matrixStack)
            return block().also {
                stack.removeAt(stack.lastIndex)
            }
        }

        fun get(): UMatrixStack = stack.lastOrNull() ?: UMatrixStack()
    }

    companion object {
        //#if MC<11700
        //$$private val MATRIX_BUFFER: FloatBuffer = GLAllocation.createDirectFloatBuffer(16)
        //#endif

        @JvmField
        val UNIT = UMatrixStack()
    }
}
