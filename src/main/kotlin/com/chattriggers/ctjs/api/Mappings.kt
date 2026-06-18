package com.chattriggers.ctjs.api

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.internal.utils.urlEncode
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.adapter.MappingNsRenamer
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch
import net.fabricmc.mappingio.format.MappingFormat
import net.fabricmc.mappingio.tree.MappingTree.ElementMapping
import net.fabricmc.mappingio.tree.MappingTree.MethodArgMapping
import net.fabricmc.mappingio.tree.MappingTreeView
import net.fabricmc.mappingio.tree.MemoryMappingTree
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.spongepowered.asm.mixin.transformer.ClassInfo
import org.spongepowered.asm.service.MixinService
import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.file.Files
import java.util.zip.ZipFile

object Mappings {
    private const val INTERMEDIARY_MAPPINGS_URL_PREFIX = "https://maven.fabricmc.net/net/fabricmc/intermediary/"
    private const val MOJMAP_VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"

    // If this is changed, also change the Java.type function in mixinProvidedLibs.js
    internal val mappedPackages = setOf("Lnet/minecraft/", "Lcom/mojang/blaze3d/")

    private val unmappedClasses = mutableMapOf<String, MappedClass>()
    private val mappedToUnmappedClassNames = mutableMapOf<String, String>()

    internal fun initialize() {
        //#if MC>=26.1
        return
        //#endif

        val minecraftVersion = FabricLoader.getInstance()
            .getModContainer("minecraft")
            .get()
            .metadata
            .version
            .friendlyString

        val jarName = "intermediary-$minecraftVersion-v2.jar".urlEncode()
        val intermediaryJarBytes = URI("${INTERMEDIARY_MAPPINGS_URL_PREFIX}$minecraftVersion/${jarName}").toURL().readBytes()
        val intermediaryTempFile = Files.createTempFile(CTJS.MOD_ID, "intermediary-mapping").toFile()
        intermediaryTempFile.writeBytes(intermediaryJarBytes)

        val intermediaryMappingBytes = ZipFile(intermediaryTempFile).use { file ->
            file.getInputStream(file.getEntry("mappings/mappings.tiny")).readAllBytes()
        }

        val intermediaryTree = MemoryMappingTree()
        MappingReader.read(ByteArrayInputStream(intermediaryMappingBytes).bufferedReader(), intermediaryTree)

        val intermediaryRenamed = MemoryMappingTree()
        intermediaryTree.accept(MappingNsRenamer(intermediaryRenamed, mapOf("official" to "obf")))

        val manifestJson = URI(MOJMAP_VERSION_MANIFEST_URL).toURL().readText()
        val versionUrl = Regex(
            """"id"\s*:\s*"${Regex.escape(minecraftVersion)}".*?"url"\s*:\s*"([^"]+)"""",
            RegexOption.DOT_MATCHES_ALL
        ).find(manifestJson)?.groupValues?.get(1) ?: error("Could not find version $minecraftVersion in manifest")

        val versionJson = URI(versionUrl).toURL().readText()
        val mojmapUrl = Regex(
            """"client_mappings".*?"url"\s*:\s*"([^"]+)"""",
            RegexOption.DOT_MATCHES_ALL
        ).find(versionJson)?.groupValues?.get(1) ?: error("Could not find client mappings in version $minecraftVersion")

        val mojmapBytes = URI(mojmapUrl).toURL().readBytes()

        val mojmapTree = MemoryMappingTree()
        MappingReader.read(
            ByteArrayInputStream(mojmapBytes).bufferedReader(),
            MappingFormat.PROGUARD_FILE,
            mojmapTree
        )

        val mojmapRenamed = MemoryMappingTree()
        mojmapTree.accept(MappingNsRenamer(mojmapRenamed, mapOf("source" to "mojmap", "target" to "obf")))

        val mojmapSwitched = MemoryMappingTree()
        mojmapRenamed.accept(MappingSourceNsSwitch(mojmapSwitched, "obf"))

        val preMerge = MemoryMappingTree()
        intermediaryRenamed.accept(preMerge)
        mojmapSwitched.accept(preMerge)

        val intermediaryNs = preMerge.getNamespaceId("intermediary")
        val mojmapNs = preMerge.getNamespaceId("mojmap")

        preMerge.classes.forEach { clazz ->
            val mojmapName = clazz.getDstName(mojmapNs) ?: return@forEach
            val intermediaryName = clazz.getDstName(intermediaryNs) ?: return@forEach

            val fields = mutableMapOf<String, MappedField>()
            clazz.fields.forEach { field ->
                val fieldMojmap = field.getDstName(mojmapNs) ?: return@forEach
                val fieldIntermediary = field.getDstName(intermediaryNs) ?: fieldMojmap
                val typeMojmap = field.getDstDesc(mojmapNs) ?: field.srcDesc
                val typeIntermediary = field.getDstDesc(intermediaryNs) ?: typeMojmap
                fields[fieldMojmap] = MappedField(
                    name = Mapping(fieldMojmap, fieldIntermediary),
                    type = Mapping(typeMojmap!!, typeIntermediary!!),
                )
            }

            val methods = mutableMapOf<String, MutableList<MappedMethod>>()
            clazz.methods.forEach { method ->
                val methodMojmap = method.getDstName(mojmapNs) ?: return@forEach
                val methodIntermediary = method.getDstName(intermediaryNs) ?: methodMojmap
                val descMojmap = method.getDstDesc(mojmapNs) ?: method.srcDesc
                val descIntermediary = method.getDstDesc(intermediaryNs) ?: descMojmap
                val mojmapType = Type.getType(descMojmap)
                val intermediaryType = Type.getType(descIntermediary)

                methods.getOrPut(methodMojmap, ::mutableListOf).add(
                    MappedMethod(
                        name = Mapping(methodMojmap, methodIntermediary),
                        parameters = method.args.sortedBy { it.lvIndex }.mapIndexed { index, param ->
                            MappedParameter(
                                Mapping(param.srcName ?: "p$index", param.srcName ?: "p$index"),
                                Mapping(
                                    mojmapType.argumentTypes[index].descriptor,
                                    intermediaryType.argumentTypes[index].descriptor,
                                ),
                                param.lvIndex,
                            )
                        },
                        returnType = Mapping(
                            mojmapType.returnType.descriptor,
                            intermediaryType.returnType.descriptor,
                        ),
                    )
                )
            }

            unmappedClasses[mojmapName] = MappedClass(
                name = Mapping(mojmapName, intermediaryName),
                fields,
                methods,
            )

            mappedToUnmappedClassNames[mojmapName] = mojmapName
            mappedToUnmappedClassNames[intermediaryName] = mojmapName
        }
    }

    internal fun getMappedClass(unmappedClassName: String): MappedClass? {
        var name = normalizeClassName(unmappedClassName)
        mappedToUnmappedClassNames[name]?.also { name = it }
        return unmappedClasses[name]
    }

    internal fun getUnmappedClass(unmappedClassName: String): MappedClass {
        val name = normalizeClassName(unmappedClassName)
        val classNode = MixinService.getService().bytecodeProvider.getClassNode(unmappedClassName)

        val fields = classNode.fields.associate {
            val type = it.desc
            val fieldName = it.name

            fieldName to MappedField(Mapping(fieldName, fieldName), Mapping(type, mapClassName(type) ?: type))
        }

        val methods = mutableMapOf<String, MutableList<MappedMethod>>()
        for (method in classNode.methods) {
            val isStatic = method.access and Opcodes.ACC_STATIC != 0
            var lvtIndex = if (isStatic) 0 else 1

            val params = mutableListOf<MappedParameter>()
            Type.getArgumentTypes(method.desc).forEachIndexed { index, type ->
                val paramType = type.descriptor
                val paramName = method.parameters?.get(index)?.name ?: return@forEachIndexed

                params.add(
                    MappedParameter(
                        Mapping(paramName, paramName),
                        Mapping(paramType, mapClassName(paramType) ?: paramType),
                        lvtIndex,
                    ),
                )

                if (type == Type.DOUBLE_TYPE || type == Type.LONG_TYPE) {
                    lvtIndex += 2
                } else {
                    lvtIndex++
                }
            }

            val returnType = Type.getReturnType(method.desc).descriptor
            val methodName = method.name
            methods.getOrPut(methodName, ::mutableListOf).add(
                MappedMethod(
                    Mapping(methodName, methodName),
                    params,
                    Mapping(returnType, mapClassName(returnType) ?: returnType),
                ),
            )
        }

        mappedToUnmappedClassNames[name] = name
        return MappedClass(Mapping(name, name), fields, methods).also {
            unmappedClasses[name] = it
        }
    }

    internal fun getMappedClassName(unmappedClassName: String) = getMappedClass(unmappedClassName)?.name?.value

    /**
     * Gets a classes unmapped class name, or throws an error if it is not mapped
     */
    @JvmStatic
    fun unmapClass(clazz: Class<*>) = unmapClassName(clazz.name)

    /**
     * Gets an unmapped class name from a mapped class name, or returns null if
     * it either does not exist or is not mapped.
     */
    @JvmStatic
    fun unmapClassName(className: String): String? = mappedToUnmappedClassNames[normalizeClassName(className)]

    /**
     * Gets the mapped class name from an unmapped class name or null if the class
     * name does not exist. Note that this is not required to use mapped classes,
     * as Rhino performs this mapping automatically during runtime.
     */
    @JvmStatic
    fun mapClassName(className: String) = getMappedClassName(className)

    private fun normalizeClassName(className: String) = (if (className.startsWith('L') && className.endsWith(';')) {
        className.drop(1).dropLast(1)
    } else {
        className
    }).replace('.', '/')

    internal data class Mapping(val original: String, val mapped: String) {
        val value: String
            get() = if (CTJS.isDevelopment) original else mapped

        companion object {
            fun fromMapped(mapped: ElementMapping) = Mapping(mapped.unmappedName, mapped.mappedName)
        }
    }

    internal data class MappedField(val name: Mapping, val type: Mapping)

    internal class MappedParameter(
        val name: Mapping,
        val type: Mapping,
        val lvtIndex: Int,
    )

    internal class MappedMethod(
        val name: Mapping,
        val parameters: List<MappedParameter>,
        val returnType: Mapping,
    ) {
        fun toDescriptor() = buildString {
            append('(')
            parameters.forEach {
                append(it.type.value)
            }
            append(')')
            append(returnType.value)
        }

        fun toFullDescriptor() = name.value + toDescriptor()
    }

    internal class MappedClass(
        val name: Mapping,
        val fields: Map<String, MappedField>,
        val methods: Map<String, List<MappedMethod>>,
    ) {
        fun findMethods(name: String, classInfo: ClassInfo?): List<MappedMethod>? {
            methods[name]?.let { return it }

            if (classInfo == null) return null

            val unmappedSuperClass = mappedToUnmappedClassNames[classInfo.superName]
            if (unmappedSuperClass != null) {
                return unmappedClasses[unmappedSuperClass]?.findMethods(name, classInfo.superClass)
            }

            val methods = mutableListOf<MappedMethod>()
            for (itf in classInfo.interfaces) {
                val unmappedInterface = mappedToUnmappedClassNames[itf] ?: continue
                unmappedClasses[unmappedInterface]?.findMethods(name, null)?.let { methods += it }
            }

            return if (methods.isEmpty()) null else methods
        }
    }

    private val ElementMapping.unmappedName: String
        get() = srcName!!

    private val ElementMapping.mappedName: String
        get() = getName("intermediary")!!

    private val MethodArgMapping.mappedName: String
        get() = unmappedName

    private val MappingTreeView.MemberMappingView.unmappedType: Type
        get() = Type.getType(srcDesc)!!

    private val MappingTreeView.MemberMappingView.mappedType: Type
        get() = Type.getType(getDesc("intermediary")!!)
}
