package dev.olshevski.navigation.reimagined

internal val <T, S> NavHostState<T, S>.hostEntries get() = snapshot.items.map { it.hostEntry }