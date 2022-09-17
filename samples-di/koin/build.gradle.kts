plugins {
    `android-application-config`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample.koin"
    defaultConfig {
        applicationId = namespace
    }
}

dependencies {
    implementation(project(":reimagined"))

    // default Compose setup
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)

    // Koin
    implementation(Libs.Koin.Compose)

    // tests
    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Test.Espresso)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.Google.Truth)
}