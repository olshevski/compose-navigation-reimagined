plugins {
    `android-library-config`
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined.hilt"
}

dependencies {
    api(projects.reimagined)
    api(libs.hilt.android)
}
