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
    }
}

rootProject.name = "compose-navigation-reimagined"
include(":reimagined")
include(":reimagined-hilt")
include(":sample")
include(":samples-di:anvil")
include(":samples-di:dagger")
include(":samples-di:hilt-assistedInject")
include(":samples-di:hilt-hiltViewModel")
include(":samples-di:koin")