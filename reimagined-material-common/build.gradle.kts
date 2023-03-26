plugins {
    `android-library-config`
    `publishing-config`
    `kotlin-parcelize`
}

android {
    namespace = "${project.group}.reimagined.material.common"
}

dependencies {
    api(project(":reimagined"))
    api(Libs.AndroidX.Compose.Foundation)
}
