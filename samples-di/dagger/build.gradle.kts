plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample.dagger"
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

    // Dagger libs
    implementation(Libs.Google.Dagger.Api)
    kapt(Libs.Google.Dagger.Compiler)
}