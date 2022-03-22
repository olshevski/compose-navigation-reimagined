package dev.olshevski.navigation.reimagined.sample.repo

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoRepositoryImpl @Inject constructor() : DemoRepository {

    init {
        Log.v("DemoRepositoryImpl", "init")
    }

}