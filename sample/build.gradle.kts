plugins {
    `android-application-config`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample"
    defaultConfig.applicationId = namespace

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi"
        )
    }
}

dependencies {
    implementation(project(":reimagined"))
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Compose.RuntimeLivedata)
    implementation(Libs.AndroidX.Compose.MaterialIconsExtended)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Ktx)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.SavedState)

    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.AndroidX.Test.Espresso)
    androidTestImplementation(Libs.AndroidX.Compose.UiTestJunit4)
    androidTestImplementation(Libs.Google.Truth)
}