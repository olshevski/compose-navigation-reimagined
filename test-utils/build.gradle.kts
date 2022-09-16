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
}