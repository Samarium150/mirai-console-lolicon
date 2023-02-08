import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
    id("com.geoffgranum.gradle-conventional-changelog") version "+"
}

group = "io.github.samarium150"
version = "6.0.7"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:2.2.3"))
    implementation("io.ktor:ktor-client-okhttp-jvm")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
}

tasks {

    withType<KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = "11"
    }

    changelog {
        appName = project.name
        versionNum = "$version"
        repoUrl = "https://github.com/Samarium150/mirai-console-lolicon"
        trackerUrl = "https://github.com/Samarium150/mirai-console-lolicon/issues"
    }
}
