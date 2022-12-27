plugins {
    `kotlin-dsl`
    id("dev.olshevski.versions") version "1.0.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("com.android.tools.build:gradle:7.3.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")

    // fix the issue with Hilt down the line: https://github.com/google/dagger/issues/3068
    implementation("com.squareup:javapoet:1.13.0")
}