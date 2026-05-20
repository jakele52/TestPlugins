package com.example

import android.content.Context
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.newExtractorLink
import org.jsoup.Jsoup

@CloudstreamPlugin
class PluginEntry: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(BoyfriendTVProvider())
    }
}

class BoyfriendTVProvider : MainAPI() {
    override var mainUrl = "https://www.boyfriendtv.com"
    override var name = "BoyfriendTV"
    override val supportedTypes = setOf(TvType.NSFW)

    override var hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val document = Jsoup.connect(mainUrl).get()
        val home = document.select(".video-item").mapNotNull {
            val title = it.selectFirst(".title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")
            
            newMovieSearchResponse(title, fixUrl(href), TvType.NSFW) {
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

            newMovieSearchResponse(title, fixUrl(href), TvType.NSFW) {
                this.posterUrl = fixUrlNull(poster)
            }
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = Jsoup.connect(url).get()
        val title = document.selectFirst("h1")?.text() ?: return null
        val poster = document.selectFirst("video")?.attr("poster")

        return newMovieLoadResponse(title, url, TvType.NSFW, url) {
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
                newExtractorLink(
                    name = "${this.name} 720p",
                    source = this.name,
                    url = fixUrl(source),
                    referer = data // CHÍNH XÁC LÀ DÒNG NÀY: Gửi kèm Link gốc để vượt qua bộ lọc bảo mật của Server
                )
            )
            return true
        }
        return false
    }
}
