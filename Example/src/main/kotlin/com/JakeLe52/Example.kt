package com.JakeLe52

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.TvType

class Example : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "Example"
    private val DEV = "DevDebug"
    private val globaltvType = TvType.Movie
}