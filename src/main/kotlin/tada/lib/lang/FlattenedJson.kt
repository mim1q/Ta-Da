package tada.lib.lang

import blue.endless.jankson.Jankson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import tada.lib.resources.MinecraftResource
import java.io.File
import java.nio.file.Path

class FlattenedJson(
  private val location: String,
  private val json: JsonObject,
  private val folder: String = "assets",
  private val separator: String = "."
) : MinecraftResource() {

  public constructor(file: File, location: String, folder: String) : this(
    location,
    file.let {
      val jankson = Jankson.builder().build()
      val parsedJson = jankson.load(it)
      val normalizedJson = parsedJson.toJson(false, false)
      return@let JsonParser.parseString(normalizedJson).getAsJsonObject()
    },
    folder
  )


  override fun generate(): JsonObject = flatten(json, separator)

  override fun getDefaultOutputDirectory(baseDir: Path, namespace: String): Path =
    baseDir.resolve("$folder/$namespace/$location")
}

internal fun flatten(json: JsonObject, separator: String = "."): JsonObject {
  val result = JsonObject()
  json.entrySet().forEach { entry ->
    if (entry.value.isJsonObject) {
      val flattened = flatten(entry.value.asJsonObject)
      flattened.entrySet().forEach {
        result.add("${entry.key}${separator}${it.key}", it.value)
      }
    } else {
      result.add(entry.key, entry.value)
    }
  }
  return result
}