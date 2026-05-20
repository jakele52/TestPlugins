package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import org.jsoup.nodes.Element

class BoyfriendTVProvider : MainAPI() {
    override var mainUrl = "https://www.boyfriendtv.com"
    override var name = "BoyfriendTV"
    override val supportedTypes = setOf(TvType.Adult)
    override var lang = "en"
    override val hasMainPage = true

    // 1. Tạo Trang Chủ (Home Page) với các danh mục: Trending, New, Top Rated
    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val items = mutableListOf<HomePageList>()
        val urls = listOf(
            Pair("Trending", "$mainUrl/trending/page$page.html"),
            Pair("Newest", "$mainUrl/newest/page$page.html"),
            Pair("Top Rated", "$mainUrl/top-rated/page$page.html")
        )
        
        for ((title, url) in urls) {
            val document = app.get(url).document
            // Tìm khối chứa danh sách video dựa trên class/id HTML của BoyfriendTV
            val videoElements = document.select("div.video-item") 
            val homeResults = videoElements.mapNotNull { it.toSearchResult() }
            if (homeResults.isNotEmpty()) {
                items.add(HomePageList(title, homeResults))
            }
        }
        return HomePageResponse(items, hasNext = true)
    }

    // Hàm phụ chuyển đổi thẻ HTML sang định dạng Search Result của CloudStream
    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("a.video-title")?.text() ?: return null
        val href = this.selectFirst("a.video-title")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        
        return newMovieSearchResponse(title, fixUrl(href), TvType.Adult) {
            this.posterUrl = fixUrlNull(posterUrl)
        }
    }

    // 2. Tìm kiếm (Search)
    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/search/videos/?q=$query"
        val document = app.get(searchUrl).document
        return document.select("div.video-item").mapNotNull { it.toSearchResult() }
    }

    // 3. Chi Tiết Video (Load nội dung trang xem phim)
    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1.watch-video-title")?.text() ?: "BoyfriendTV Video"
        val poster = document.selectFirst("video")?.attr("poster")

        return newMovieLoadResponse(title, url, TvType.Adult, url) {
            this.posterUrl = fixUrlNull(poster)
        }
    }

    // 4. Lấy Link Stream (.mp4 hoặc m3u8) để chạy trong Player
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        
        // Trích xuất link video từ thẻ <source> trong thẻ <video>
        val videoUrl = document.selectFirst("video source")?.attr("src")
        
        if (!videoUrl.isNullOrEmpty()) {
            callback.invoke(
                ExtractorLink(
                    name = "BoyfriendTV Direct",
                    source = "BoyfriendTV",
                    url = fixUrl(videoUrl),
                    referer = mainUrl,
                    quality = Qualities.Unknown.value // Hoặc tự bóc tách độ phân giải nếu có
                )
            )
            return true
        }
        return false
    }
}
