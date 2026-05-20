import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "com.android.library")
apply(plugin = "kotlin-android")
apply(plugin = "com.lagradost.cloudstream3.gradle")

extensions.getByType(com.lagradost.cloudstream3.gradle.CloudstreamExtension::class.java).apply {
    setRepo(System.getenv("GITHUB_REPOSITORY") ?: "jakele52/TestPlugins")
}

extensions.getByType(com.android.build.gradle.BaseExtension::class.java).apply {
    namespace = "com.example"
    compileSdkVersion(35)

    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType(KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xno-call-assertions",
            "-Xno-param-assertions",
            "-Xno-receiver-assertions",
            "-Xskip-metadata-version-check"
        )
    }
}

dependencies {
    add("cloudstream", "com.lagradost:cloudstream3:pre-release")
    add("implementation", kotlin("stdlib"))
    add("implementation", "com.github.Blatzar:NiceHttp:0.4.11")
    add("implementation", "org.jsoup:jsoup:1.18.3")
    add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
}