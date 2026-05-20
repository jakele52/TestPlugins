package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import org.jsoup.Jsoup

class BoyfriendTVProvider : MainAPI() {
    override var mainUrl = "https://www.boyfriendtv.com"
    override var name = "BoyfriendTV"
    override val supportedTypes = setOf(TvType.Adult)

    override var hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val document = Jsoup.connect(mainUrl).get()
        val home = document.select(".video-item").mapNotNull {
            val title = it.selectFirst(".title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")
            
            newMovieSearchResponse(title, fixUrl(href), TvType.Adult) {
                this.posterUrl = fixUrlNull(poster)
            }
        }
        return newHomePageResponse(listOf(HomePageList("Recent Videos", home)), false)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = Jsoup.connect("$mainUrl/search/videos/?search_query=$query").get()
        return document.select(".video-item").mapNotNull {
            val title = it.selectFirst(".title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")

            newMovieSearchResponse(title, fixUrl(href), TvType.Adult) {
                this.posterUrl = fixUrlNull(poster)
            }
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = Jsoup.connect(url).get()
        val title = document.selectFirst("h1")?.text() ?: return null
        val poster = document.selectFirst("video")?.attr("poster")

        return newMovieLoadResponse(title, url, TvType.Adult, url) {
            this.posterUrl = fixUrlNull(poster)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = Jsoup.connect(data).get()
        val source = document.selectFirst("video source")?.attr("src")

        if (!source.isNullOrEmpty()) {
            callback(
                ExtractorLink(
                    name = this.name,
                    source = this.name,
                    url = fixUrl(source),
                    referer = data,
                    quality = Qualities.P720.value
                )
            )
            return true
        }
        return false
    }
}
