plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.material3"
}

dependencies {
    api(project(":reimagined-material-common"))
    api(Libs.AndroidX.Compose.Material3)
}
