plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("PackagingPlugin") {
            id = "koinz.gradle.packaging.PackagingPlugin"
            implementationClass = "koinz.gradle.packaging.PackagingPlugin"
        }
    }
}

dependencies {
    implementation(project(":gradle-tasks"))
    implementation(libs.commons.codec)
}
