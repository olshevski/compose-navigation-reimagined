import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.config)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)

    // version catalog access from precompiled scripts
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    // fix the issue with Hilt down the line: https://github.com/google/dagger/issues/3068
    implementation(libs.javapoet)
}