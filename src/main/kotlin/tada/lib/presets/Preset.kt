package tada.lib.presets

import tada.lib.resources.MinecraftResource
import tada.lib.generator.ResourceGenerator.GeneratorEntry

/**
 * Represents a collection of Resources with their names that should be generated as part of a single preset
 *
 * @param setup function to apply to the preset on creation
 */
class Preset(setup: Preset.() -> Unit = {}) {
  val entries = mutableListOf<GeneratorEntry>()

  init {
    this.apply(setup)
  }

  /**
   * Adds a new entry to the preset
   *
   * @param entry [GeneratorEntry] to add to the preset
   */
  fun add(entry: GeneratorEntry) {
    entries.add(entry)
  }

  /**
   * Adds a new entry constructed from the provided name and resource
   *
   * @param name the name of the entry
   * @param resource the resource of the entry
   */
  fun add(name: String, resource: MinecraftResource) {
    entries.add(GeneratorEntry(name, resource))
  }


  /**
   * Adds the entries provided by another preset to this preset
   *
   * @param preset [Preset] whose entries will be added to this preset
   */
  fun add(preset: Preset) {
    entries.addAll(preset.entries)
  }

  /**
   * Removes all entries with the provided names from the preset
   *
   * @param names names of entries to remove
   * @return this [Preset]
   */
  fun exclude(vararg names: String): Preset {
    entries.removeAll { names.contains(it.name) }
    return this
  }
}