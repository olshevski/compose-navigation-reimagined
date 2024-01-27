plugins {
    `android-application-config`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample.koin"
}

dependencies {
    implementation(projects.reimagined)
    implementation(libs.koin.compose)
    androidTestImplementation(projects.testUtils)
}