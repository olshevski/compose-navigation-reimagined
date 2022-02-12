plugins {
    plugin(Plugins.Android.Library)
    plugin(Plugins.Kotlin.Android)
    plugin(Plugins.Kotlin.Parcelize)
    `maven-publish`
    signing
    plugin(Plugins.Dokka)
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
    api(Libs.AndroidX.Compose.Ui)
    api(Libs.AndroidX.Compose.Animation)
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

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    from((android.sourceSets["main"].kotlin as com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet).srcDirs)
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("api") {
                from(components["release"])
                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set("Compose Navigation Reimagined")
                    description.set("A small and simple, yet fully fledged and customizable navigation library for Jetpack Compose")
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