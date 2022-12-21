package dev.olshevski.navigation.reimagined

internal fun <T, S> NavHostStateImpl<T, S>.findHostEntry(destination: T) =
    hostEntriesMap.values.find { it.destination == destination }!!