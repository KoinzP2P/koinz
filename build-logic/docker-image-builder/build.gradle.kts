plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("DockerImageBuilderPlugin") {
            id = "koinz.gradle.docker.image_builder.DockerImageBuilderPlugin"
            implementationClass = "koinz.gradle.docker.image_builder.DockerImageBuilderPlugin"
        }
    }
}
