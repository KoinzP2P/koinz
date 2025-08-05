plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("AppStartPlugin") {
            id = "koinz.gradle.app_start_plugin.AppStartPlugin"
            implementationClass = "koinz.gradle.app_start_plugin.AppStartPlugin"
        }
    }
}
