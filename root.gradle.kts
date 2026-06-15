plugins {
    kotlin("jvm") version "2.4.0" apply false // Don't bump, depends on preprocessor
    id("gg.essential.multi-version.root")
}

preprocess.strictExtraMappings.set(true)
preprocess {
    val fabric26_01_02 = createNode("26.1.2-fabric", 26_01_02, "srg")
    val fabric12111 = createNode("1.21.11-fabric", 12111, "yarn")
    val fabric12110 = createNode("1.21.10-fabric", 12110, "yarn")

    fabric26_01_02.link(fabric12111)
    fabric12111.link(fabric12110)
}

subprojects {
    afterEvaluate {
        tasks.findByName("preprocessTestCode")?.enabled = false
    }
}
