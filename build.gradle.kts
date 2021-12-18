import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.8.3"
    id("org.jetbrains.dokka") version "1.6.0"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "io.github.samarium150"
version = "5.0.0-beta.2"

repositories {
    mavenLocal()
    maven(url="https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.5.4")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(true)
            includes.from("Module.md")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(
                    URL("https://github.com/Samarium150/mirai-console-lolicon/tree/main/src/main/kotlin")
                )
                remoteLineSuffix.set("#L")
            }
            jdkVersion.set(11)
            externalDocumentationLink {
                url.set(URL("https://kdoc.mirai.mamoe.net/2.9.0-RC"))
                packageListUrl.set(URL("https://kdoc.mirai.mamoe.net/2.9.0-RC/package-list"))
            }
        }
    }
}

changelog {
    appName = project.name
    versionNum = "$version"
    repoUrl = "https://github.com/Samarium150/mirai-console-lolicon"
    trackerUrl = "https://github.com/Samarium150/mirai-console-lolicon/issues"
}
