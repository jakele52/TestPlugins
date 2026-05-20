package com.example

import com.lagradost.cloudstream3.*

class ExampleProvider : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "Example"
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun search(query: String): List<SearchResponse> {
        return emptyList()
    }
}
