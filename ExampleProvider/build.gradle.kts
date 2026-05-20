import com.android.build.gradle.BaseExtension
import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.lagradost.cloudstream3.gradle")
}

// SỬA: Thêm "extensions." để định danh chính xác phạm vi cấu hình của Cloudstream
extensions.configure<CloudstreamExtension> {
    setRepo(System.getenv("GITHUB_REPOSITORY") ?: "jakele52/TestPlugins")
}

// SỬA: Thêm "extensions." để định danh chính xác phạm vi cấu hình của Android SDK
extensions.configure<BaseExtension> {
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

tasks.withType<KotlinCompile>().configureEach {
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
    // SỬA: Sử dụng chuỗi định danh cấu hình gốc (String literal invocation) để tránh lỗi khởi tạo trễ
    "cloudstream"("com.lagradost:cloudstream3:pre-release")
    "implementation"(kotlin("stdlib"))
    "implementation"("com.github.Blatzar:NiceHttp:0.4.11")
    "implementation"("org.jsoup:jsoup:1.18.3")
    "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
}
