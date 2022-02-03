package dev.olshevski.navigation.reimagined

val <T> NavBackstack<T>.destinations get() = entries.map { it.destination }