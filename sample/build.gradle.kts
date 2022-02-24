plugins {
    plugin(Plugin.Android.Application)
    plugin(Plugin.Kotlin.Android)
    plugin(Plugin.Kotlin.Parcelize)
}

android {
    compileSdk = AndroidSdkVersion.Compile

    defaultConfig {
        applicationId = "dev.olshevski.navigation.reimagined.sample"
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

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Lib.AndroidX.Compose.CompilerVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":reimagined"))
    implementation(Lib.AndroidX.Activity.Compose)
    implementation(Lib.AndroidX.Compose.Material)
    debugImplementation(Lib.AndroidX.Compose.UiTooling)
    implementation(Lib.AndroidX.Compose.UiToolingPreview)
    implementation(Lib.AndroidX.Compose.RuntimeLivedata)
    implementation(Lib.AndroidX.Compose.MaterialIconsExtended)
    implementation(Lib.AndroidX.Lifecycle.ViewModel.Ktx)
    implementation(Lib.AndroidX.Lifecycle.ViewModel.Compose)
    implementation(Lib.AndroidX.Lifecycle.ViewModel.SavedState)

    testImplementation(Lib.Kotest.RunnerJunit5)
    testImplementation(Lib.Kotest.FrameworkDataset)

    androidTestImplementation(Lib.AndroidX.Test.Core)
    androidTestImplementation(Lib.AndroidX.Compose.UiTestJunit4)
}