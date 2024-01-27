plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.material"
}

dependencies {
    api(projects.reimaginedMaterialCommon)
    api(libs.compose.material)
}
