package com.appat.graphicov.utilities

import android.app.Application
import com.kieronquinn.monetcompat.core.MonetCompat

class GraphiCovApplication: Application() {

    companion object {
        var app: GraphiCovApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        MonetCompat.enablePaletteCompat()
        app = this
    }
}