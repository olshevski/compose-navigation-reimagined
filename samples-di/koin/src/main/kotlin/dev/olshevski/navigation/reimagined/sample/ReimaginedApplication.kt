package dev.olshevski.navigation.reimagined.sample

import android.app.Application
import dev.olshevski.navigation.reimagined.sample.repo.RepositoryModule
import dev.olshevski.navigation.reimagined.sample.ui.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ReimaginedApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ReimaginedApplication)
            modules(RepositoryModule, ViewModelModule)
        }
    }
}