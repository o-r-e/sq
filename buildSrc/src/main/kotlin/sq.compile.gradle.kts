plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
}

group = property("project.group")!!.toString()
version = property("project.version")!!.toString()

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

java {
    withSourcesJar()
    withJavadocJar()
}
kotlin {
    jvmToolchain(property("java.version")!!.toString().toInt())
}

tasks.compileKotlin {
    this.compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
}

tasks.jar {
    manifest {
        attributes(mapOf(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
        ))
    }
}
