import java.util.*

enum class VersionStabilityLevel {
    ALPHA,
    BETA,
    RC,
    RELEASE,
    UNKNOWN
}

fun stabilityLevel(version: String) = version.toLowerCase(Locale.ROOT).let { lowercaseVersion ->
    when {
        isRelease(lowercaseVersion) -> VersionStabilityLevel.RELEASE
        listOf("rc", "m").any { lowercaseVersion.contains(it) } -> VersionStabilityLevel.RC
        lowercaseVersion.contains("beta") -> VersionStabilityLevel.BETA
        lowercaseVersion.contains("alpha") -> VersionStabilityLevel.ALPHA
        else -> VersionStabilityLevel.UNKNOWN.also {
            println("Unknown stability level: $version")
        }
    }
}

private fun isRelease(lowercaseVersion: String) =
    listOf("release", "final", "ga").any { lowercaseVersion.contains(it) }
            || "^[0-9,.v-]+(-r)?$".toRegex().matches(lowercaseVersion)
