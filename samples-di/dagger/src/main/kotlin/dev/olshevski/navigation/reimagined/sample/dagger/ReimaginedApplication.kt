package dev.olshevski.navigation.reimagined.sample.dagger

import android.app.Application
import android.content.Context

class ReimaginedApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }

}

val Context.appComponent get() = (applicationContext as ReimaginedApplication).appComponent