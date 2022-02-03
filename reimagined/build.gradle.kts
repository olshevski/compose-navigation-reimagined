plugins {
    plugin(Plugins.Android.Library)
    plugin(Plugins.Kotlin.Android)
    plugin(Plugins.Kotlin.Parcelize)
}

android {
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
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
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
}

dependencies {
    api(Libs.AndroidX.Activity.Compose)
    api(Libs.AndroidX.Compose.Material)
    debugApi(Libs.AndroidX.Compose.UiTooling)
    api(Libs.AndroidX.Compose.UiToolingPreview)
    api(Libs.AndroidX.Lifecycle.ViewModel.Ktx)
    api(Libs.AndroidX.Lifecycle.ViewModel.Compose)
    api(Libs.AndroidX.Lifecycle.ViewModel.SavedState)

    testImplementation(Libs.Kotest.RunnerJunit5)
    testImplementation(Libs.Kotest.FrameworkDataset)

    // for latest "kotest-extensions-robolectric" sources included into the code, until the proper
    // working version is published
    testImplementation(kotlin("reflect"))
    testImplementation(Libs.Robolectric)
    testImplementation(Libs.JUnit4)
}