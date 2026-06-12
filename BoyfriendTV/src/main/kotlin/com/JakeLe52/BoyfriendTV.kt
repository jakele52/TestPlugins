package com.JakeLe52

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.mvvm.logError
import com.lagradost.cloudstream3.network.WebViewResolver
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

private fun okhttp3.Headers.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    for (i in 0 until this.size) {
        map[this.name(i)] = this.value(i)
    }
    return map
}

class BoyfriendTV : MainAPI() {
    private val globalTvType = TvType.NSFW

    override var mainUrl = "https://www.boyfriendtv.com"
    override var name = "BoyfriendTV"
    override val hasMainPage = true
    override val hasChromecastSupport = true
    override val hasDownloadSupport = true
    override val vpnStatus = VPNStatus.MightBeNeeded
    override val supportedTypes = setOf(TvType.NSFW)

    override val mainPage = mainPageOf(
        "$mainUrl/latest-updates/" to "Latest Updates",
        "$mainUrl/most-popular/" to "Most Popular",
        "$mainUrl/top-rated/" to "Top Rated",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val pagedLink = if (page > 1) "${request.data}$page/" else request.data
        val soup = app.get(pagedLink).document
        val home = soup.select("div.thumb, .thumb-item").mapNotNull {
            it.toSearchResult()
        }
        
        return newHomePageResponse(
            list = HomePageList(
                name = request.name,
                list = home,
                isHorizontalImages = true
            ),
            hasNext = true
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search/?q=$query"
        val document = app.get(url).document
        return document.select("div.thumb, .thumb-item").mapNotNull {
            it.toSearchResult()
        }.distinctBy { it.url }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst(".thumb-title, .title")?.text() 
            ?: this.selectFirst("a")?.attr("title") 
            ?: return null
        val link = fixUrlNull(this.selectFirst("a")?.attr("href")) ?: return null
        val img = fetchImgUrl(this.selectFirst("img"))
        
        return MovieSearchResponse(
            name = title,
            url = link,
            apiName = this@BoyfriendTV.name,
            type = globalTvType,
            posterUrl = img
        )
    }

    override suspend fun load(url: String): LoadResponse {
        val soup = app.get(url).document
        val title = soup.selectFirst("h1.video-title, .video-details h1")?.text() ?: ""
        val poster = soup.selectFirst("head meta[property=og:image]")?.attr("content")
        val tags = soup.select(".video-details a[href*='/tags/'], .categories a")
            .map { it.text().trim() }
            .filter { it.isNotBlank() }
            
        return MovieLoadResponse(
            name = title,
            url = url,
            apiName = this.name,
            type = globalTvType,
            dataUrl = url,
            posterUrl = poster,
            tags = tags,
            plot = title
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        // BoyfriendTV often uses a player that loads media via JS.
        // We catch the media request using WebViewResolver.
        app.get(
            url = data,
            interceptor = WebViewResolver(
                Regex(".*\\.(?:mp4|m3u8)(?:\\?.*)?")
            )
        ).let { response ->
            if (response.url.contains(".m3u8")) {
                M3u8Helper().m3u8Generation(
                    M3u8Helper.M3u8Stream(
                        response.url,
                        headers = response.headers.toMap()
                    ), true
                ).forEach { stream ->
                    callback(
                        ExtractorLink(
                            source = name,
                            name = "$name M3U8",
                            url = stream.streamUrl,
                            referer = mainUrl,
                            quality = getQualityFromName(stream.quality?.toString()),
                            isM3u8 = true
                        )
                    )
                }
            } else {
                callback(
                    ExtractorLink(
                        source = name,
                        name = "$name MP4",
                        url = response.url,
                        referer = mainUrl,
                        quality = Qualities.Unknown.value,
                    )
                )
            }
        }
        return true
    }

    private fun fetchImgUrl(imgsrc: Element?): String? {
        return try { 
            imgsrc?.attr("data-src")
            ?: imgsrc?.attr("data-original")
            ?: imgsrc?.attr("data-thumb_url")
            ?: imgsrc?.attr("src")
        } catch (e: Exception) { null }
    }
}
