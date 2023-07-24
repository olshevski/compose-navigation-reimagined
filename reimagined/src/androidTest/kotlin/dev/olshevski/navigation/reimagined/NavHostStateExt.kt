package dev.olshevski.navigation.reimagined

@OptIn(ExperimentalReimaginedApi::class)
internal fun <T> NavHostState<T>.findHostEntry(destination: T) =
    hostEntries.find { it.destination == destination }!!