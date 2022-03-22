package dev.olshevski.navigation.reimagined.sample

fun String.singleLine(separator: String = " ") = lineSequence()
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .joinToString(separator)