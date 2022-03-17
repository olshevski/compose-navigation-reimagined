plugins {
    plugin(Plugin.Android.Library)
    plugin(Plugin.Kotlin.Android)
    plugin(Plugin.Kotlin.Parcelize)
    `maven-publish`
    signing
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
        kotlinCompilerExtensionVersion = Lib.AndroidX.Compose.CompilerVersion
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
    api(Lib.AndroidX.Activity.Compose)
    api(Lib.AndroidX.Compose.Ui)
    api(Lib.AndroidX.Compose.Animation)
    api(Lib.AndroidX.Lifecycle.ViewModel.Ktx)
    api(Lib.AndroidX.Lifecycle.ViewModel.Compose)
    api(Lib.AndroidX.Lifecycle.ViewModel.SavedState)

    testImplementation(Lib.JUnit.Juniper)
    testImplementation(Lib.Google.Truth)

    androidTestImplementation(Lib.AndroidX.Test.Runner)
    androidTestImplementation(Lib.Google.Truth)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("api") {
                from(components["release"])

                pom {
                    name.set("Compose Navigation Reimagined")
                    description.set("Type-safe navigation library for Jetpack Compose")
                    url.set("https://github.com/olshevski/compose-navigation-reimagined")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/olshevski/compose-navigation-reimagined/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("olshevski")
                            name.set("Vitali Olshevski")
                            email.set("tech@olshevski.dev")
                            url.set("https://olshevski.dev")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/olshevski/compose-navigation-reimagined.git")
                        developerConnection.set("scm:git:https://github.com/olshevski/compose-navigation-reimagined.git")
                        url.set("https://github.com/olshevski/compose-navigation-reimagined")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        project.properties["signing.key"].toString(),
        project.properties["signing.password"].toString(),
    )
    sign(publishing.publications)
}