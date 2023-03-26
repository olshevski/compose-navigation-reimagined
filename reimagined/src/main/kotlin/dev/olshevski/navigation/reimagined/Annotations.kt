package dev.olshevski.navigation.reimagined

@RequiresOptIn(
    message = "This is experimental API of Navigation Reimagined library. It may be subject to change.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalReimaginedApi

@RequiresOptIn(
    message = "This is internal API of Navigation Reimagined library. It is not intended to be used outside of the library itself.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalReimaginedApi