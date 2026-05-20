dependencies {
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}

// Số phiên bản của Plugin (Tăng số này lên nếu bạn cập nhật code sau này)
version = 1

cloudstream {
    description = "Provider for BoyfriendTV"
    authors = listOf("Jakele52")

    /**
    * Trạng thái nguồn:
    * 0: Down (Lỗi/Sập)
    * 1: Ok (Hoạt động tốt)
    * 2: Slow (Chậm)
    * 3: Beta-only
    **/
    status = 1 

    // SỬA TẠI ĐÂY: Đổi sang "Adult" để khớp với TvType.Adult trong file BoyfriendTVProvider.kt
    tvTypes = listOf("Adult")

    requiresResources = true
    language = "en"

    // Icon hiển thị của Plugin trong app CloudStream
    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/2/2f/Korduene_Logo.png"
}

android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}
