plugins {
    kotlin("jvm") version "1.8.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

group = "dev.haato"
version = "1.0"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        autoCorrect = true
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        testImplementation("io.kotest:kotest-assertions-core-jvm:5.6.2")
        testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.2")

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}