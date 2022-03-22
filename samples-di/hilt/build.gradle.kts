plugins {
    plugin(Plugins.Android.Application)
    plugin(Plugins.Kotlin.Android)
    plugin(Plugins.Kotlin.Kapt)
    plugin(Plugins.Kotlin.Parcelize)
    plugin(Plugins.Hilt)
}

android {
    compileSdk = AndroidSdkVersion.Compile

    defaultConfig {
        applicationId = "dev.olshevski.navigation.reimagined.sample.hilt"
        minSdk = AndroidSdkVersion.Min
        targetSdk = AndroidSdkVersion.Target
        versionName = project.property("version").toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    signingConfigs {
        named("debug") {
            storeFile = rootProject.file("debug.keystore")
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.CompilerVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":reimagined"))
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Ktx)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.SavedState)
    implementation(Libs.Google.Dagger.HiltAndroid)
    kapt(Libs.Google.Dagger.HiltCompiler)
    implementation(Libs.Olshevski.EasyFactoriesCompose)
}

kapt {
    correctErrorTypes = true
}