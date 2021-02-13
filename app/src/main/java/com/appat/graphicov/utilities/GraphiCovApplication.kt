package com.appat.graphicov.utilities

import android.app.Application

class GraphiCovApplication: Application() {

    companion object {
        var app: GraphiCovApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}