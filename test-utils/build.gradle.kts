plugins {
    `android-library-config`
}

android {
    namespace = "${project.group}.testutils"
}

dependencies {
    api(Libs.AndroidX.Activity.Compose)
    api(Libs.AndroidX.Compose.Runtime)
    api(Libs.AndroidX.Lifecycle.ViewModel.Api)
    api(Libs.AndroidX.Compose.UiTestJunit4)
    api(Libs.AndroidX.Test.Espresso)

    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestManifest)
    androidTestImplementation(Libs.Google.Truth)
}