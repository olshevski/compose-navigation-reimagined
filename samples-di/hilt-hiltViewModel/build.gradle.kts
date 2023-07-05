plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
    plugin(Plugins.Hilt)
}

android {
    namespace = "${project.group}.reimagined.sample.hilt.hiltviewmodel"
}

dependencies {
    implementation(project(":reimagined-hilt"))

    // default Compose setup
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)

    // Hilt libs
    implementation(Libs.Google.Dagger.HiltAndroid)
    kapt(Libs.Google.Dagger.HiltCompiler)

    // tests
    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Test.Espresso)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.Google.Truth)
}

kapt {
    correctErrorTypes = true
}