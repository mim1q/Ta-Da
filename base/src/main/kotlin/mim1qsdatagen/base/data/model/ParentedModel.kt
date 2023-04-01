package mim1qsdatagen.base.data.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import mim1qsdatagen.base.data.MinecraftData
import java.nio.file.Path

/**
 * A class representing a Minecraft model of a block or item
 *
 * @param type the type of the model (either [Type.BLOCK] or [Type.ITEM]])
 * @param parent the parent model
 */
class ParentedModel internal constructor(
  private val type: Type,
  private val parent: String
) : MinecraftData {
  /**
   * Map of the texture overrides for this model, where each key is the name and the value is the texture path
   */
  private val textureOverrides = mutableMapOf<String, String>()

  /**
   * Adds a texture override to the model
   *
   * @param key the name of the texture to override
   * @param texture path to the texture file
   * @return this [ParentedModel] instance
   */
  fun texture(key: String, texture: String): ParentedModel {
    textureOverrides[key] = texture
    return this
  }

  override fun generate(): JsonElement {
    return JsonObject().apply {
      addProperty("parent", parent)
      if (textureOverrides.isNotEmpty()) {
        add("textures", JsonObject().apply {
          textureOverrides.forEach {
            addProperty(it.key, it.value)
          }
        })
      }
    }
  }

  override fun getDefaultOutputPath(baseDir: Path, namespace: String): Path {
    return baseDir.resolve("assets/$namespace/models/${type.folderName}/")
  }

  /**
   * Model types, which determine whether a model should go into the items or blocks folder
   */
  enum class Type(val folderName: String) {
    ITEM("item"),
    BLOCK("block")
  }
}