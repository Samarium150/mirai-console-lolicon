import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    val kotlinVersion = "1.6.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.0"
    id("org.jetbrains.dokka") version "1.6.0"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "io.github.samarium150"
version = "5.1.0"

repositories {
    mavenLocal()
    maven(url="https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.6.5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "org.slf4j")
        exclude(group = "io.ktor", module = "ktor-client-core")
    }
}

tasks {

    withType<KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = "11"
    }

    withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            configureEach {
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
                    url.set(URL("https://kdoc.mirai.mamoe.net/2.10.0"))
                    packageListUrl.set(URL("https://kdoc.mirai.mamoe.net/2.10.0/package-list"))
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
}
