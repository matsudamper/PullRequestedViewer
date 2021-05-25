import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.10"
    id("com.apollographql.apollo") version "2.5.3"
}

group = "me.matsudamper"
version = "1.0"

repositories {
    jcenter()
    gradlePluginPortal()
    mavenCentral()
}

apollo {
    generateKotlinModels.set(true)
    customTypeMapping.set(
        mapOf(
            "URI" to "java.net.URI",
            "DateTime" to "java.util.Calendar"
        )
    )
}

dependencies {
    implementation(project(":common"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    api("com.apollographql.apollo:apollo-runtime:2.5.3")
    implementation("com.apollographql.apollo:apollo-runtime-kotlin:2.5.3")
    implementation("com.apollographql.apollo:apollo-coroutines-support:2.5.3")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.annotation.AnnotationTarget.ExperimentalStdlibApi"
}
