package dev.olshevski.navigation.reimagined

internal val <T, S> NavHostState<T, S>.hostEntries get() = targetSnapshot.items.map { it.hostEntry }