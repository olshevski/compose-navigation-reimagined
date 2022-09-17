package dev.olshevski.navigation.reimagined.sample.koin

import android.app.Application
import dev.olshevski.navigation.reimagined.sample.koin.repo.RepositoryModule
import dev.olshevski.navigation.reimagined.sample.koin.ui.ViewModelModule
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