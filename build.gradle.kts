import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("com.github.recloudstream:gradle:-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    configure {
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "user/repo")
    }

    configure {
        namespace = "com.example"
        compileSdkVersion(35)

        defaultConfig {
            minSdk = 21
            targetSdk = 35
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xno-receiver-assertions",
                "-Xskip-metadata-version-check"
            )
        }
    }

    dependencies {
        "cloudstream"("com.lagradost:cloudstream3:pre-release")
        "implementation"(kotlin("stdlib"))
        "implementation"("com.github.Blatzar:NiceHttp:0.4.11")
        "implementation"("org.jsoup:jsoup:1.18.3")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    }
}

task("clean") {
    delete(rootProject.layout.buildDirectory)
}
