plugins {
    `android-application-config`
    `kotlin-parcelize`
    `kotlin-kapt`
    alias(libs.plugins.hilt)
}

android {
    namespace = "${project.group}.reimagined.sample.hilt.hiltviewmodel"
}

dependencies {
    implementation(projects.reimaginedHilt)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    androidTestImplementation(projects.testUtils)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}