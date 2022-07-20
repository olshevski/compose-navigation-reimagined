package dev.olshevski.navigation.reimagined

internal interface NavHostStateScope<out T> {

    fun getNavHostEntry(navId: NavId): NavHostEntry<T>?

}