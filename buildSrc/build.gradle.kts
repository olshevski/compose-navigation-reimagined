plugins {
    `kotlin-dsl`
    id("dev.olshevski.versions") version "1.0.1"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}