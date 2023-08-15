import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

data class PluginSpec(val id: String, val version: String)

fun PluginDependenciesSpec.plugin(pluginSpec: PluginSpec) {
    id(pluginSpec.id) version (pluginSpec.version)
}

object Plugins {

    val Anvil = PluginSpec("com.squareup.anvil", "2.4.7")
    val Hilt = PluginSpec("com.google.dagger.hilt.android", Libs.Google.Dagger.Version)
    val NexusPublishing = PluginSpec("io.github.gradle-nexus.publish-plugin", "1.3.0")
    val Versions = PluginSpec("dev.olshevski.versions", "1.0.5")

}