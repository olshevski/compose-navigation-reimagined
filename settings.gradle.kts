pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "compose-navigation-reimagined"
include(":test-utils")
include(":reimagined")
include(":reimagined-hilt")
include(":reimagined-material-common")
include(":reimagined-material")
include(":reimagined-material3")
include(":sample")
include(":samples-di:anvil")
include(":samples-di:dagger")
include(":samples-di:hilt-assistedInject")
include(":samples-di:hilt-hiltViewModel")
include(":samples-di:koin")
