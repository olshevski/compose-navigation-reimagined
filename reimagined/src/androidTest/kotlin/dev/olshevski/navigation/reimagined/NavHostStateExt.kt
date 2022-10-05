package dev.olshevski.navigation.reimagined

internal val <T> NavHostState<T>.hostEntries get() = targetSnapshot.items.map { it.hostEntry }