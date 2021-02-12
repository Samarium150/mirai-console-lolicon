import java.net.URL

plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.4-RC"
    id("org.jetbrains.dokka") version "1.4.20"
}

group = "com.github.samarium150"
version = "3.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    implementation("com.google.code.gson:gson:+")
    implementation("com.github.kittinunf.fuel:fuel:+")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(true)
            includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(
                    URL("https://github.com/Samarium150/mirai-lolicon-api/blob/master/src/main/kotlin")
                )
                remoteLineSuffix.set("#L")
            }
        }
    }
}
