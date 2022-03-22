object Libs {

    object AndroidX {

        object Activity {
            const val Compose = "androidx.activity:activity-compose:1.4.0"
        }

        object Compose {
            private const val Version = "1.1.1"

            @Suppress("MemberVisibilityCanBePrivate")
            const val CompilerVersion = Version

            const val Ui = "androidx.compose.ui:ui:$Version"
            const val Animation = "androidx.compose.animation:animation:$Version"
            const val Material = "androidx.compose.material:material:$Version"
            const val UiTooling = "androidx.compose.ui:ui-tooling:$Version"
            const val UiToolingPreview = "androidx.compose.ui:ui-tooling-preview:$Version"
            const val UiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$Version"
            const val RuntimeLivedata = "androidx.compose.runtime:runtime-livedata:$Version"
            const val MaterialIconsExtended =
                "androidx.compose.material:material-icons-extended:$Version"
        }

        object Lifecycle {
            private const val Version = "2.4.1"

            object ViewModel {
                const val Ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$Version"
                const val Compose = "androidx.lifecycle:lifecycle-viewmodel-compose:$Version"
                const val SavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$Version"
            }
        }

        object Test {
            const val Runner = "androidx.test:runner:1.4.0"
            const val Espresso = "androidx.test.espresso:espresso-core:3.4.0"
        }
    }

    object Google {
        const val Truth = "com.google.truth:truth:1.1.3"

        object Dagger {
            const val Version = "2.41"
            const val Api = "com.google.dagger:dagger:$Version"
            const val Compiler = "com.google.dagger:dagger-compiler:$Version"
            const val HiltAndroid = "com.google.dagger:hilt-android:$Version"
            const val HiltCompiler = "com.google.dagger:hilt-compiler:$Version"
        }
    }

    object JUnit {
        const val Juniper = "org.junit.jupiter:junit-jupiter:5.8.2"
    }

    object Olshevski {
        const val EasyFactoriesCompose = "dev.olshevski.viewmodel:easy-factories-compose:1.0.0"
    }

}