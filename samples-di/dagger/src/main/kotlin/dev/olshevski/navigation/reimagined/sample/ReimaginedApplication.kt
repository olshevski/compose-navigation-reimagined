package dev.olshevski.navigation.reimagined.sample

import android.app.Application
import android.content.Context

class ReimaginedApplication : Application() {

    val appComponent by lazy {
        DaggerAppComponent.create()
    }

}

val Context.appComponent get() = (applicationContext as ReimaginedApplication).appComponent