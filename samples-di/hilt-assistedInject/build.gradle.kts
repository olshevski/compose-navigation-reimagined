plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
    alias(libs.plugins.hilt)
}

android {
    namespace = "${project.group}.reimagined.sample.hilt.assistedinject"
}

dependencies {
    implementation(projects.reimagined)
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