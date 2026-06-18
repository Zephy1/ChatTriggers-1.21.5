import org.gradle.kotlin.dsl.support.unzipTo
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import java.net.HttpURLConnection
import java.net.URI

buildscript {
    dependencies {
        classpath(libs.versioning)
    }
}

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

if (!project.hasProperty("full")) {
    project.gradle.startParameter.excludedTaskNames.add("kspKotlin")
}

group = "com.chattriggers.ctjs"
version = property("mod_version").toString()

repositories {
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://repo.essential.gg/repository/maven-public")
}

dependencies {
    implementation(project(":rhino"))
    include(project(":rhino"))

    val universalCraftVersion = project.findProperty("universalcraft").toString()
    modImplementation(include("gg.essential:universalcraft-${universalCraftVersion}:${libs.versions.universalcraft.get()}")!!)
    implementation(include("gg.essential:vigilance:${libs.versions.vigilance.get()}")!!)
    implementation(include("gg.essential:elementa:${libs.versions.elementa.get()}")!!)
    modImplementation(libs.bundles.included) { include(this) }

    val modmenuVersion = project.findProperty("modmenu").toString()
    modApi("com.terraformersmc:modmenu:$modmenuVersion")

    modRuntimeOnly(libs.devauth)
    dokkaPlugin(libs.versioning)

    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":typing-generator"))
    ksp(project(":typing-generator"))
}

loom {
    val local = file("src/main/resources/ctjs.accesswidener")
    val global = file("../../src/main/resources/ctjs.accesswidener")
    accessWidenerPath.set(if (local.exists()) local else global)

    runConfigs {
        named("client") {
            ideConfigGenerated(true)
            programArgs("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }
}

base {
    archivesName.set(property("archives_base_name") as String)
}

tasks {
    processResources {
        val minecraftVersion = project.platform.mcVersionStr
        val version = project.version
        val minFabricApiVersion = project.findProperty("min-fabric-api")?.toString()

        inputs.property("version", version)
        inputs.property("minecraftVersion", minecraftVersion)
        inputs.property("min_fabric_api_version", minFabricApiVersion.toString())

        filesMatching("fabric.mod.json") {
            val props = mutableMapOf(
                "version" to version,
                "minecraftVersion" to minecraftVersion,
                "min_fabric_api_version" to minFabricApiVersion,
            )
            expand(props)
        }

        val javaVersion = project.java.toolchain.languageVersion.get().asInt()
        inputs.property("compatibilityLevel", javaVersion)
        filesMatching("ctjs.mixins.json") {
            filter { line ->
                line.replace("JAVA_\$compatibilityLevel", "JAVA_$javaVersion")
            }
        }
    }

    jar {
        from("LICENSE") {
            rename { "${name}_${base.archivesName.get()}" }
        }
    }

    dokkaHtml {
        // Just use the module name here since the MC version doesn't affect CT's API across the same mod version
        moduleVersion.set(project.version.toString())
        moduleName.set("ctjs")

        val docVersionsDir = projectDir.resolve("build/javadocs")
        val currentVersion = project.version.toString()
        val currentDocsDir = docVersionsDir.resolve(currentVersion)
        outputs.upToDateWhen { docVersionsDir.exists() }

        outputDirectory.set(file(currentDocsDir))

        pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
            version = project.version.toString()
            olderVersionsDir = docVersionsDir
            renderVersionsNavigationOnAllPages = true
        }

        suppressObviousFunctions.set(true)
        suppressInheritedMembers.set(true)

        val branch = getBranch()
        dokkaSourceSets {
            configureEach {
                perPackageOption {
                    matchingRegex.set("com\\.chattriggers\\.ctjs\\.internal(\$|\\.).*")
                    suppress.set(true)
                }

                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(URI("https://github.com/ChatTriggers/ctjs/blob/$branch/src/main/kotlin").toURL())
                    remoteLineSuffix.set("#L")
                }

                externalDocumentationLink {
                    val yarnVersion = project.findProperty("yarn").toString()

                    url.set(URI("https://maven.fabricmc.net/docs/yarn-$yarnVersion/").toURL())
                    packageListUrl.set(URI("https://maven.fabricmc.net/docs/yarn-$yarnVersion/element-list").toURL())
                }
            }
        }

        doFirst {
            val archiveBase = "https://www.chattriggers.com/javadocs-archive/"
            val versions = String(downloadFile(archiveBase + "versions")).lines().map(String::trim)
            val tmpFile = File(temporaryDir, "oldVersionsZip.zip")

            versions.filter(String::isNotEmpty).map(String::trim).forEach { version ->
                val zipBytes = downloadFile("$archiveBase$version.zip")
                tmpFile.writeBytes(zipBytes)
                unzipTo(docVersionsDir, tmpFile)
            }

            tmpFile.delete()
        }

        doLast {
            // At this point we have a structure that looks something like this:
            // javadocs
            //   \-- 2.2.0-1.8.9
            //   \-- 3.0.0
            //         \-- older
            //
            // The "older" directory contains all old versions, so we want to
            // delete the top-level older versions and move everything inside the
            // latest directory to the top level so the GitHub actions workflow
            // doesn't need to figure out the correct version name

            docVersionsDir.listFiles()?.forEach {
                if (it.name != version) {
                    it.deleteRecursively()
                }
            }

            val latestVersionDir = docVersionsDir.listFiles()!!.single()
            latestVersionDir.listFiles()!!.forEach {
                it.renameTo(File(it.parentFile.parentFile, it.name))
            }
            latestVersionDir.deleteRecursively()
        }
    }
}

fun downloadFile(url: String): ByteArray {
    return (URI(url).toURL().openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        doOutput = true
    }.inputStream.readAllBytes()
}

fun getBranch(): String {
    return ProcessBuilder("git", "rev-parse", "HEAD")
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
        .trim()
}

afterEvaluate {
    val hasRemapJar = tasks.findByName("remapJar") != null
    val outputTaskName = if (hasRemapJar) "remapJar" else "jar"

    tasks.register<Copy>("collectJars") {
        group = "build"
        description = "Copies this version's non-shadowed JARs to main/jars"

        val outputDir = projectDir.resolve("../../jars").normalize()
        dependsOn(outputTaskName)

        from(tasks.named(outputTaskName)) {
            include("*.jar")
            exclude { it.name.contains(" 1.2") && it.name.contains("-all") }
            rename {
                "${rootProject.name}-${version}+${project.platform.mcVersionStr}.jar"
            }
        }
        into(outputDir)
    }

    tasks.named("build") {
        finalizedBy("collectJars")
    }

    configurations.named("default") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }

    artifacts {
        add("default", tasks.named(outputTaskName))
    }
}
