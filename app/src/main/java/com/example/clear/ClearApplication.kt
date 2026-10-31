package com.example.clear

import android.app.Application
import android.content.res.Configuration

/** Bestimmt die Sprache, bevor irgendetwas angezeigt wird, auch fuer Erinnerungen im Hintergrund. */
class ClearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLanguage.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppLanguage.init(this)
    }
}
