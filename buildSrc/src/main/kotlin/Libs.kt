object Libs {

    object AndroidX {

        object Activity {
            const val Compose = "androidx.activity:activity-compose:1.4.0"
        }

        object Compose {
            private const val Version = "1.1.0"

            @Suppress("MemberVisibilityCanBePrivate")
            const val CompilerVersion = Version

            const val Material = "androidx.compose.material:material:$Version"
            const val UiTooling = "androidx.compose.ui:ui-tooling:$Version"
            const val UiToolingPreview = "androidx.compose.ui:ui-tooling-preview:$Version"
            const val UiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$Version"
            const val RuntimeLivedata = "androidx.compose.runtime:runtime-livedata:$Version"
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
            const val Core = "androidx.test:core:1.4.0"
        }
    }

    const val JUnit4 = "junit:junit:4.13.2"

    object Kotest {
        private const val Version = "5.1.0"
        const val RunnerJunit5 = "io.kotest:kotest-runner-junit5:$Version"
        const val FrameworkDataset = "io.kotest:kotest-framework-datatest:$Version"
    }

    const val Robolectric = "org.robolectric:robolectric:4.7.3"

}