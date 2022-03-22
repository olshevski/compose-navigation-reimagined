package dev.olshevski.navigation.reimagined.sample.repo

import org.koin.dsl.module

val RepositoryModule = module {

    single<DemoRepository> { DemoRepositoryImpl() }

}