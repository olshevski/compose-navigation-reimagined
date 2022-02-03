import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    plugin(Plugins.BenManesVersions)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        stabilityLevel(currentVersion) > stabilityLevel(candidate.version)
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}