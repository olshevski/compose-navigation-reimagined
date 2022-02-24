import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    plugin(Plugin.BenManesVersions)
    plugin(Plugin.NexusPublishing)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        stabilityLevel(currentVersion) > stabilityLevel(candidate.version)
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}