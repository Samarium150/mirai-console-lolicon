import java.net.URL

plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.5"
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "com.github.samarium150"
version = "3.4.1"

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
            includes.from("Module.md")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(
                    URL("https://github.com/Samarium150/mirai-console-lolicon/tree/master/src/main/kotlin")
                )
                remoteLineSuffix.set("#L")
            }
            jdkVersion.set(11)
        }
    }
}

changelog {
    appName = project.name
    versionNum = "$version"
    repoUrl = "https://github.com/Samarium150/mirai-console-lolicon"
    trackerUrl = "https://github.com/Samarium150/mirai-console-lolicon"
}
