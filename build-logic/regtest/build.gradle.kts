plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("RegtestPlugin") {
            id = "koinz.gradle.regtest_plugin.RegtestPlugin"
            implementationClass = "koinz.gradle.regtest_plugin.RegtestPlugin"
        }
    }
}
