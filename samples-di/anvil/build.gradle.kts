plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
    alias(libs.plugins.anvil)
}

android {
    namespace = "${project.group}.reimagined.sample.anvil"
}

dependencies {
    implementation(projects.reimagined)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    androidTestImplementation(projects.testUtils)
}