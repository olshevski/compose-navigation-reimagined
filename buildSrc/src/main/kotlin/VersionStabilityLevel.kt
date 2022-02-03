import java.util.*

enum class VersionStabilityLevel {
    UNKNOWN,
    ALPHA,
    BETA,
    RC,
    RELEASE
}

fun stabilityLevel(version: String) = version.toLowerCase(Locale.ROOT).let { lowercaseVersion ->
    when {
        isRelease(lowercaseVersion) -> VersionStabilityLevel.RELEASE
        lowercaseVersion.contains("rc") -> VersionStabilityLevel.RC
        lowercaseVersion.contains("beta") -> VersionStabilityLevel.BETA
        lowercaseVersion.contains("alpha") -> VersionStabilityLevel.ALPHA
        else -> VersionStabilityLevel.UNKNOWN
    }
}

private fun isRelease(lowercaseVersion: String) =
    listOf("release", "final", "ga").any { lowercaseVersion.contains(it) }
            || "^[0-9,.v-]+(-r)?$".toRegex().matches(lowercaseVersion)
