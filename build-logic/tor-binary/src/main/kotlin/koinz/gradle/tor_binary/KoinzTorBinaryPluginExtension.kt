package koinz.gradle.tor_binary

import org.gradle.api.provider.Property

abstract class KoinzTorBinaryPluginExtension {
    abstract val version: Property<String>
}