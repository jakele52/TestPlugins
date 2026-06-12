import com.lagradost.cloudstream3.gradle.CloudstreamExtension 
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        // Shitpack repo which contains our tools and dependencies
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        // Cloudstream gradle plugin which makes everything work and builds plugins
        classpath("com.github.recloudstream:gradle:81b1d424d2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        // when running through github workflow, GITHUB_REPOSITORY should contain current repository name
        setRepo("https://github.com/jakele52/TestPlugins")

        description = "For the coomers and degenerates"
        authors = listOf("Jace")
    }

    android {
        compileSdkVersion(30)

        defaultConfig {
            minSdk = 21
            targetSdk = 30
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8" // Required
                // Disables some unnecessary features
                freeCompilerArgs = freeCompilerArgs +
                        "-Xno-call-assertions" +
                        "-Xno-param-assertions" +
                        "-Xno-receiver-assertions"
            }
        }
    }

    dependencies {
        val apk by configurations.creating
        // Stubs for all Cloudstream classes
        // apk("com.lagradost:cloudstream3:pre-release") // Broken on JitPack

        add("compileOnly", "com.github.recloudstream.cloudstream:library:v4.7.0")
        add("compileOnly", kotlin("stdlib")) 

        // những dependency này có thể bao gồm bất kỳ cái nào được app thêm vào,
        // nhưng bạn không cần thêm tất cả nếu không dùng đến
        add("implementation", "com.github.Blatzar:NiceHttp:0.4.11")
        add("implementation", "org.jsoup:jsoup:1.15.3") 
        add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
        add("implementation", "io.karn:khttp-android:0.1.2")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
