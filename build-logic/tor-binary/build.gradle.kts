plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("KoinzTorBinaryPlugin") {
            id = "koinz.gradle.tor_binary.KoinzTorBinaryPlugin"
            implementationClass = "koinz.gradle.tor_binary.KoinzTorBinaryPlugin"
        }
    }
}

dependencies {
    implementation(project(":gradle-tasks"))
}
