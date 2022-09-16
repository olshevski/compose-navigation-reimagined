plugins {
    `android-library`
    `maven-publish`
    signing
}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

fun createPublicationName() = name.split("-")
    .mapIndexed { index, s ->
        if (index == 0) s else s.capitalize()
    }
    .joinToString(separator = "")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>(createPublicationName()) {
                from(components["release"])

                pom {
                    name.set("Compose Navigation Reimagined")
                    description.set("Type-safe navigation library for Jetpack Compose")
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