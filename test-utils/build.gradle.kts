plugins {
    `android-library-config`
}

android {
    namespace = "${project.group}.testutils"
}

dependencies {
    api(platform(Libs.AndroidX.Compose.Bom))
    api(Libs.AndroidX.Activity.Compose)
    api(Libs.AndroidX.Compose.Runtime)
    api(Libs.AndroidX.Lifecycle.ViewModel.Api)
    api(Libs.AndroidX.Compose.UiTestJunit4)
    api(Libs.AndroidX.Test.Espresso)

    androidTestImplementation(platform(Libs.AndroidX.Compose.Bom))
    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestManifest)
    androidTestImplementation(Libs.Google.Truth)
}