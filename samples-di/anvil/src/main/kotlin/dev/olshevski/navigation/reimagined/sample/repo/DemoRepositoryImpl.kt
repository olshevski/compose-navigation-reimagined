package dev.olshevski.navigation.reimagined.sample.repo

import android.util.Log
import com.squareup.anvil.annotations.ContributesBinding
import dev.olshevski.navigation.reimagined.sample.AppScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppScope::class)
class DemoRepositoryImpl @Inject constructor() : DemoRepository {

    init {
        Log.v("DemoRepositoryImpl", "init")
    }

}