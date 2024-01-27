plugins {
    `android-library-config`
    `publishing-config`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.material.common"
}

dependencies {
    api(projects.reimagined)
    api(libs.compose.foundation)
}
