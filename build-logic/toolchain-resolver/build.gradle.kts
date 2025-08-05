plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("ToolchainResolverPlugin") {
            id = "koinz.gradle.toolchain_resolver.ToolchainResolverPlugin"
            implementationClass = "koinz.gradle.toolchain_resolver.ToolchainResolverPlugin"
        }
    }
}
