package com.JakeLe52

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.TvType

class Example : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "Example"
    override val supportedTypes = setOf(TvType.Movie)
}