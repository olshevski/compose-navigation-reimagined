plugins {
    plugin(Plugins.Android.Application)
    plugin(Plugins.Kotlin.Android)
    plugin(Plugins.Kotlin.Parcelize)
}

android {
    namespace = "${project.group}.reimagined.sample.koin"
    compileSdk = AndroidSdkVersion.Compile

    defaultConfig {
        applicationId = namespace
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
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
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

    // default Compose setup
    implementation(Libs.AndroidX.Activity.Compose)
    implementation(Libs.AndroidX.Compose.Material)
    debugImplementation(Libs.AndroidX.Compose.UiTooling)
    implementation(Libs.AndroidX.Compose.UiToolingPreview)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Ktx)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.Compose)
    implementation(Libs.AndroidX.Lifecycle.ViewModel.SavedState)

    // Koin
    implementation(Libs.Koin.Compose)
}