plugins {
    `android-application-config`
    `kotlin-kapt`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.sample.dagger"
}

dependencies {
    implementation(projects.reimagined)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    androidTestImplementation(projects.testUtils)
}