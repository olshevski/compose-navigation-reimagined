plugins {
    `android-library-config`
    `publishing-config`
    `kotlin-parcelize`
    `compose-compiler-reports`
}

android {
    namespace = "${project.group}.reimagined"
}

dependencies {
    api(libs.activity.compose)
    api(platform(libs.compose.bom))
    api(libs.compose.animation)
    api(libs.lifecycle.viewmodel.compose)
    androidTestImplementation(projects.testUtils)
}