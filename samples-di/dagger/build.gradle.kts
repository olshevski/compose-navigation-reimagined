plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample.dagger"
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

    // Dagger libs
    implementation(Libs.Google.Dagger.Api)
    kapt(Libs.Google.Dagger.Compiler)

    // tests
    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Test.Espresso)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.Google.Truth)
}