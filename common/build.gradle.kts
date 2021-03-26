import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.functions

plugins {
    kotlin("jvm")
}

group = "me.matsudamper"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
}


dependencies {
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=androidx.compose.material.ripple.ExperimentalRippleApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.annotation.AnnotationTarget.ExperimentalStdlibApi"
}
