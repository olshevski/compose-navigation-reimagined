plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.hilt"
}

dependencies {
    api(project(":reimagined"))
    api(Libs.Google.Dagger.HiltAndroid)
}
