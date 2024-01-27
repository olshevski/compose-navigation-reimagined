plugins {
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.config)
    alias(libs.plugins.nexus.publish)
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.layout.buildDirectory)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}