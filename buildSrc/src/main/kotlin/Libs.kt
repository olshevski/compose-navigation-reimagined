object Libs {

    object AndroidX {

        object Activity {
            const val Compose = "androidx.activity:activity-compose:1.6.1"
        }

        object Compose {
            const val CompilerVersion = "1.3.2"
            const val Bom = "androidx.compose:compose-bom:2022.12.00"
            const val Animation = "androidx.compose.animation:animation"
            const val Material = "androidx.compose.material:material"
            const val MaterialIconsExtended = "androidx.compose.material:material-icons-extended"
            const val Runtime = "androidx.compose.runtime:runtime"
            const val Ui = "androidx.compose.ui:ui"
            const val UiTooling = "androidx.compose.ui:ui-tooling"
            const val UiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
            const val UiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
            const val UiTestManifest = "androidx.compose.ui:ui-test-manifest"
            const val UiUtil = "androidx.compose.ui:ui-util"
        }

        object Lifecycle {
            private const val Version = "2.5.1"

            object ViewModel {
                const val Api = "androidx.lifecycle:lifecycle-viewmodel:$Version"
                const val Compose = "androidx.lifecycle:lifecycle-viewmodel-compose:$Version"
            }
        }

        object Test {
            const val Espresso = "androidx.test.espresso:espresso-core:3.5.0"
            const val Runner = "androidx.test:runner:1.5.0"
        }
    }

    object Google {
        const val Truth = "com.google.truth:truth:1.1.3"

        object Dagger {
            const val Version = "2.44.2"
            const val Api = "com.google.dagger:dagger:$Version"
            const val Compiler = "com.google.dagger:dagger-compiler:$Version"
            const val HiltAndroid = "com.google.dagger:hilt-android:$Version"
            const val HiltCompiler = "com.google.dagger:hilt-compiler:$Version"
        }
    }

    object JUnit {
        const val Juniper = "org.junit.jupiter:junit-jupiter:5.9.1"
    }

    object Koin {
        const val Compose = "io.insert-koin:koin-androidx-compose:3.4.0"
    }

}