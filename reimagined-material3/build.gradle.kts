plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.material3"
}

dependencies {
    api(projects.reimaginedMaterialCommon)
    api(libs.compose.material3)
}
