plugins {
    `android-library-config`
}

android {
    namespace = "${project.group}.testutils"
}

dependencies {
    api(platform(libs.compose.bom))
    api(libs.activity.compose)
    api(libs.compose.runtime)
    api(libs.lifecycle.viewmodel)
    api(libs.compose.ui.test.junit4)
    api(libs.espresso.core)
}