pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

}
rootProject.name = "review_requested"

data class Projects(
    val name: String,
    val dir: String
)
listOf(
    Projects(":github", "/github"),
    Projects(":repository-github", "/repository/github"),
    Projects(":repository-local", "/repository/local"),
    Projects(":compose", "/compose"),
    Projects(":common", "/common")
).onEach {
    include(it.name)
    project(it.name).projectDir = File(rootDir, it.dir)
}
