package dev.olshevski.navigation.reimagined.sample.hilt.assistedinject.repo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bind(demoRepositoryImpl: DemoRepositoryImpl): DemoRepository

}