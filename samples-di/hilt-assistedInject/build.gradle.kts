plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
    plugin(Plugins.Hilt)
}

android {
    namespace = "${project.group}.reimagined.sample.hilt.assistedinject"
    defaultConfig.applicationId = namespace
}

dependencies {
    implementation(project(":reimagined"))

    // default Compose setup
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)

    // Hilt libs
    implementation(Libs.Google.Dagger.HiltAndroid)
    kapt(Libs.Google.Dagger.HiltCompiler)
}

kapt {
    correctErrorTypes = true
}