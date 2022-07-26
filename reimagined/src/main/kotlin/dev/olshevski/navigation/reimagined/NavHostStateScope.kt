package dev.olshevski.navigation.reimagined

internal interface NavHostStateScope<out T> {

    fun getHostEntry(id: NavId): NavHostEntry<T>?

}