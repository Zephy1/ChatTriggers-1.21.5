plugins {
    kotlin("jvm") version "2.3.10" apply false // Don't bump, depends on preprocessor
    id("gg.essential.multi-version.root")
}

preprocess.strictExtraMappings.set(true)
preprocess {
    val fabric26_02_00 = createNode("26.2-fabric", 26_02_00, "official")
    val fabric26_01_02 = createNode("26.1.2-fabric", 26_01_02, "official")
    val fabric12111 = createNode("1.21.11-fabric", 12111, "yarn")

    fabric26_02_00.link(fabric26_01_02)
    fabric26_01_02.link(fabric12111)
}

subprojects {
    afterEvaluate {
        tasks.findByName("preprocessTestCode")?.enabled = false
    }
}
