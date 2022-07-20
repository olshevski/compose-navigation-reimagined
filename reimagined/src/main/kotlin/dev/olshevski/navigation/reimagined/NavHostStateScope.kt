package dev.olshevski.navigation.reimagined

internal interface NavHostStateScope<T> {

    fun getNavHostEntry(navId: NavId): NavHostEntry<T>?

}