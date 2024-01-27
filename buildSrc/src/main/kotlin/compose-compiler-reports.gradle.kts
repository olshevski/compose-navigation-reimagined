plugins {
    `android-library`
    `kotlin-android`
}

android {
    kotlinOptions {
        // use "-PenableComposeCompilerReports=true" to enable
        if ((project.findProperty("enableComposeCompilerReports") as? String).toBoolean()) {
            val outputDir = project.layout.buildDirectory.dir("compose-reports").get()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$outputDir",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$outputDir"
            )
        }
    }
}