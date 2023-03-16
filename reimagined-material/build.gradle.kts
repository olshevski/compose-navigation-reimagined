plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.material"
}

dependencies {
    api(project(":reimagined-material-common"))
    api(Libs.AndroidX.Compose.Material)
}
