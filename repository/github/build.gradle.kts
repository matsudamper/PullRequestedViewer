import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "me.matsudamper"
version = "1.0"

repositories {
    jcenter()
    gradlePluginPortal()
    mavenCentral()
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":github"))
    implementation(project(":common"))

    implementation("org.koin:koin-core")
    implementation("org.koin:koin-core-ext")
    implementation("org.koin:koin-test")
    
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.annotation.AnnotationTarget.ExperimentalStdlibApi"
}
