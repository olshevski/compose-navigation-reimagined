package dev.olshevski.navigation.reimagined.sample.repo

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bind(demoRepositoryImpl: DemoRepositoryImpl): DemoRepository

}