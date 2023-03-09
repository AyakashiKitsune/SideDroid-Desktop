import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.ayakashi.SideDriod"
version = "1.3.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("junit:junit:4.13.2")
                implementation("net.java.dev.jna:jna-platform-jpms:5.12.1")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SideDroid-Desktop"
            packageVersion = "1.3.0"
        }
    }
}
tasks.withType<Jar> {
    doFirst {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        val main by kotlin.jvm().compilations.getting
        manifest {
            attributes(
                "Main-Class" to "MainKt",
            )
        }
        from({
            main.runtimeDependencyFiles.files.filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}