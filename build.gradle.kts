import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.functions

plugins {
    kotlin("jvm") version "1.4.30"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "me.matsudamper"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

val koin_version = "2.2.2"
val dependencyNameAndVersion = mapOf(
    "org.koin:koin-core" to koin_version,
    "org.koin:koin-core-ext" to koin_version,
    "org.koin:koin-test" to koin_version
)

dependencies {
    implementation(project(":repository-github"))
    implementation(project(":repository-local"))
    implementation(project(":compose"))

    implementation("org.koin:koin-core")
    implementation("org.koin:koin-core-ext")
    implementation("org.koin:koin-test")
}

allprojects {
    repositories {
        jcenter()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        if (configurations.findByName("implementation") == null) {
            configurations.create("implementation")
        }
        configurations {
            getByName("implementation") {
                dependencies {
                    dependencyNameAndVersion.forEach { name, version ->
                        dependency("${name}:${version}")
                    }
                }
            }
        }
//        configurations.all {
//            println(this)
//            resolutionStrategy.eachDependency {
//                println("$this")
//                println(this::class.members.map { it.name })
//            }

//            println(this.findByName("implementation"))
//        }
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=androidx.compose.material.ripple.ExperimentalRippleApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.annotation.AnnotationTarget.ExperimentalStdlibApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.contracts.ExperimentalContracts"
}
