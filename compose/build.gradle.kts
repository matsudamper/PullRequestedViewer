import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.4.0-build168"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":repository-github"))
    implementation(project(":repository-local"))

    val koin_version = "2.2.2"
    implementation("org.koin:koin-core:$koin_version")
    implementation("org.koin:koin-core-ext:$koin_version")
    testImplementation("org.koin:koin-test:$koin_version")

    implementation("net.java.dev.jna:jna:5.7.0")
    implementation("net.java.dev.jna:jna-platform:5.7.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=androidx.compose.material.ripple.ExperimentalRippleApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.annotation.AnnotationTarget.ExperimentalStdlibApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.InternalCoroutinesApi"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            macOS {

            }

            linux {

            }

            windows {

            }

            packageName = "review_requested"
        }
    }
}