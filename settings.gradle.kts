pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
}

// !! This uses my own fork of the toolkit, I couldn't get 1.21.9+ to build on the maven build (I couldn't update past Loom 1.9.x due to depreciated methods) !!
includeBuild("../essential-gradle-toolkit")
include(":typing-generator")
include("rhino")
project(":rhino").projectDir = file("../rhino")
rootProject.name = "ctjs"
rootProject.buildFileName = "root.gradle.kts"

val versionList = listOf(
    "26.2-fabric",
    "26.1.2-fabric",
    "1.21.11-fabric",
)
versionList.forEach { version ->
    file("versions/$version").mkdirs()
}

versionList.forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
