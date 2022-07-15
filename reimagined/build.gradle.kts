plugins {
    plugin(Plugins.Android.Library)
    plugin(Plugins.Kotlin.Android)
    plugin(Plugins.Kotlin.Parcelize)
    `publishing-config`
}

android {
    namespace = "${project.group}.reimagined"
    compileSdk = AndroidSdkVersion.Compile

    defaultConfig {
        minSdk = AndroidSdkVersion.Min
        targetSdk = AndroidSdkVersion.Target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.CompilerVersion
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    api(Libs.AndroidX.Activity.Compose)
    api(Libs.AndroidX.Compose.Ui)
    api(Libs.AndroidX.Compose.Animation)
    api(Libs.AndroidX.Lifecycle.ViewModel.Ktx)
    api(Libs.AndroidX.Lifecycle.ViewModel.Compose)
    api(Libs.AndroidX.Lifecycle.ViewModel.SavedState)

    testImplementation(Libs.JUnit.Juniper)
    testImplementation(Libs.Google.Truth)

    androidTestImplementation(Libs.AndroidX.Test.Runner)
    androidTestImplementation(Libs.Google.Truth)
}