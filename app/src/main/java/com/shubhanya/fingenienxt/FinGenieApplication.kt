package com.shubhanya.fingenienxt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinGenieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // You can initialize other app-wide things here if needed
    }
}
