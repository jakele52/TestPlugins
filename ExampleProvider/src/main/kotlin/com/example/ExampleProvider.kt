pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PRESERVE_EXISTING)
}

rootProject.name = "TestPlugins"
include(":ExampleProvider")
