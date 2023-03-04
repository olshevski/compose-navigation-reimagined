pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}

rootProject.name = "compose-navigation-reimagined"
include(":test-utils")
include(":reimagined")
include(":reimagined-hilt")
include(":reimagined-material")
include(":sample")
include(":samples-di:anvil")
include(":samples-di:dagger")
include(":samples-di:hilt-assistedInject")
include(":samples-di:hilt-hiltViewModel")
include(":samples-di:koin")
