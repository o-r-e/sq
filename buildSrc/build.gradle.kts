plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.github.spotbugs.snom:spotbugs-gradle-plugin:4.7.5")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
}
