plugins {
    `android-library-config`
    `kotlin-parcelize`
    `publishing-config`
    `compose-compiler-reports`
}

android {
    namespace = "${project.group}.reimagined"
}

dependencies {
    api(Libs.AndroidX.Activity.Compose)
    api(Libs.AndroidX.Compose.Ui)
    api(Libs.AndroidX.Compose.Animation)
    api(Libs.AndroidX.Lifecycle.ViewModel.Compose)

    testImplementation(Libs.JUnit.Juniper)
    testImplementation(Libs.Google.Truth)

    androidTestImplementation(project(":test-utils"))
    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestManifest)
    androidTestImplementation(Libs.Google.Truth)
}