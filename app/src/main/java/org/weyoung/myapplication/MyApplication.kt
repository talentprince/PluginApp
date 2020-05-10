package org.weyoung.myapplication

import android.app.Application

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PluginLoader.load(this)
    }
}