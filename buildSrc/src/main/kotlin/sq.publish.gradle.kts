plugins {
    `maven-publish`
    signing
}

val mavenPublicationName = "mavenJava"
fun isSnapshotVersion(): Boolean = version.toString().uppercase().endsWith("SNAPSHOT")

val sonatypeUsername: String? = project.properties["sonatype.username"]?.toString()
val sonatypePassword: String? = project.properties["sonatype.password"]?.toString()

publishing {
    publications {
        create<MavenPublication>(mavenPublicationName) {
            from(components["java"])
            pom {
                name.set("SQ")
                description.set("SQL builder for Kotlin")
                url.set("https://github.com/o-r-e/sq")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("o-r-e")
                        name.set("Roman Obuhov")
                        email.set("obuhov.r@gmail.com")
                        organization.set("Roman Obuhov @ Github")
                        organizationUrl.set("https://github.com/o-r-e")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/o-r-e/sq.git")
                    developerConnection.set("scm:git:ssh://github.com:o-r-e/sq.git")
                    url.set("https://github.com/o-r-e/sq/tree/master")
                }
            }
        }
    }

    repositories {
        maven localBuildDir@ {
            val repoUrl = if (isSnapshotVersion()) {
                uri(layout.buildDirectory.dir("repo-snapshot"))
            } else {
                uri(layout.buildDirectory.dir("repo-release"))
            }

            name = "localBuildDir"
            url = repoUrl
        }

        // Sonatype
        if ((sonatypeUsername != null) && (sonatypePassword != null)) {
            maven ossrh@ {
                val repoUrl = if (isSnapshotVersion()) {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                } else {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }

                name = "sonatype"
                url = repoUrl

                this.credentials {
                    this.username = sonatypeUsername
                    this.password = sonatypePassword
                }
            }
        }
    }
}

signing {
    sign(publishing.publications[mavenPublicationName])
}
