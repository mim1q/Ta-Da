package tada.lib.data.generator

import com.google.gson.JsonElement
import tada.lib.data.MinecraftResource
import tada.lib.data.presets.Preset
import java.nio.file.Path

/**
 * Generator of the JSON files containing data provided by [MinecraftResource] objects.
 *
 * @param namespace the namespace of the generator (usually the ID of your mod / datapack)
 * @param baseDirectory path to the base directory of the generated data
 * @param fileSaver provides strategies to save the generated [JsonElement] Strings and to prepare the base directory
 * @param jsonFormatter provides a String formatting strategy for JSON data
 */
open class ResourceGenerator(
  private val namespace: String,
  private val baseDirectory: Path,
  private val fileSaver: FileSaver,
  private val jsonFormatter: JsonFormatter
) {
  /**
   * List of the provided [MinecraftResource]s with their respective [String] names
   */
  private val entries: MutableList<GeneratorEntry> = mutableListOf()

  /**
   * Add a resource to the generator
   *
   * @param name the name of the resource
   * @param resource the [MinecraftResource] to add to [entries]
   */
  fun add(name: String, resource: MinecraftResource) {
    entries.add(GeneratorEntry(name, resource))
  }

  fun add(preset: Preset) {
    for (entry in preset.entries) {
      entries.add(entry)
    }
  }

  /**
   * Generate the single provided resource with the provided [fileSaver] and [jsonFormatter] strategies
   *
   * @param name the name of the resource
   * @param resource the [MinecraftResource] to generate
   */
  private fun generateResource(name: String, resource: MinecraftResource) {
    val filePath = resource.getDefaultOutputDirectory(baseDirectory, namespace).resolve("$name.json")
    fileSaver.save(filePath, jsonFormatter.format(resource.generate()))
  }

  /**
   * Generate all the previously added resources, previously preparing
   */
  fun generate() {
    fileSaver.prepare(baseDirectory)
    for (entry in entries) {
      generateResource(entry.name, entry.resource)
    }
    fileSaver.finish(baseDirectory)
  }

  companion object {
    /**
     * Creates a default [ResourceGenerator]
     *
     * @param namespace the namespace of the generator (usually the ID of your mod / datapack)
     * @param baseDirectory path to the base directory of the generated data
     * @return a [ResourceGenerator] instance with the given [namespace] and [baseDirectory] path, using the
     * [FilesystemFileSaver] for the output strategy and [BeautifiedJsonFormatter] for the JSON-to-String formatting
     * strategy
     */
    fun create(namespace: String, baseDirectory: Path): ResourceGenerator {
      return ResourceGenerator(namespace, baseDirectory, FilesystemFileSaver, BeautifiedJsonFormatter)
    }
  }

  /**
   * Represents a [MinecraftResource] with its assigned name
   */
  data class GeneratorEntry(val name: String, val resource: MinecraftResource)

  /**
   * Defines file saving and base directory preparation strategies
   */
  interface FileSaver {
    /**
     * Prepares the root directory that will contain the generated resources
     *
     * @param baseDirectory path to the base directory of the generated pack
     */
    fun prepare(baseDirectory: Path)

    /**
     * Writes the provided content to a file in the given path
     *
     * @param filePath path to the file to generate
     * @param content content to write to the file
     */
    fun save(filePath: Path, content: String)

    /**
     * Runs after all the files have been generated
     *
     * @param baseDirectory path to the base directory of the generated pack
     */
    fun finish(baseDirectory: Path) { }
  }

  /**
   * Defines the JSON-to-string conversion strategy
   */
  interface JsonFormatter {
    /**
     * Converts the given JSON Element to a string
     *
     * @param json the [JsonElement] to convert
     * @return the String representation of the provided JSON Element
     */
    fun format(json: JsonElement): String
  }
}