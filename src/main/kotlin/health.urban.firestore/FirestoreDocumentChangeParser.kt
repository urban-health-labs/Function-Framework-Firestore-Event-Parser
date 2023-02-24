package health.urban.firestore

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

data class DocumentChange<T> (var oldValue: T,
                              var value: T)

inline fun <reified T>String.parseDocumentChange(): DocumentChange<T> =
  Gson().let { gson ->
    gson.fromJson<JsonObject>(this)
      .let { jsonObject ->
        DocumentChange(
          gson.fromJson<T>(jsonObject.get("oldValue").parseJsonObject),
          gson.fromJson<T>(jsonObject.get("value").parseJsonObject)
        )
      }
  }

val JsonElement.parseJsonObject: JsonObject get() = JsonObject().also { jsonObject ->
  asJsonObject.get("fields")?.asJsonObject?.also { fieldsObject ->
    fieldsObject.keySet()?.forEach { fieldKey ->
      fieldsObject.get(fieldKey).asJsonObject?.also { valueJsonObject ->
        valueJsonObject.keySet().forEach { valueKey ->
          when (valueKey) {
            "stringValue" -> jsonObject.addProperty(fieldKey, valueJsonObject.get(valueKey).asString)
            "integerValue" -> jsonObject.addProperty(fieldKey, valueJsonObject.get(valueKey).asLong)
            "timestampValue" -> jsonObject.addProperty(fieldKey, valueJsonObject.get(valueKey).asString)
            "booleanValue" -> jsonObject.addProperty(fieldKey, valueJsonObject.get(valueKey).asBoolean)
            "mapValue" -> jsonObject.add(fieldKey, valueJsonObject.get(valueKey).parseJsonObject)
            "arrayValue" -> jsonObject.add(fieldKey, valueJsonObject.get(valueKey).parseJsonArray)
            "referenceValue" -> jsonObject.addProperty(fieldKey, valueJsonObject.get(valueKey).asString)
          }
        }
      }
    }
  }
}

private val JsonElement.parseJsonArray get() = JsonArray().also { jsonArray ->
  asJsonObject.get("values").asJsonArray.forEach { jsonElement ->
    jsonElement.asJsonObject.also { jsonObject ->
      jsonObject.keySet().forEach { valueKey ->
        when (valueKey) {
          "stringValue" -> jsonArray.add(jsonObject.get(valueKey).asString)
          "integerValue" -> jsonArray.add(jsonObject.get(valueKey).asLong)
          "timestampValue" -> jsonArray.add(jsonObject.get(valueKey).asString)
          "booleanValue" -> jsonArray.add(jsonObject.get(valueKey).asBoolean)
          "mapValue" -> jsonArray.add(jsonObject.get(valueKey).parseJsonObject)
          "referenceValue" -> jsonArray.add(jsonObject.get(valueKey).asString)
        }
      }
    }
  }
}

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJson(json: JsonObject) = fromJson<T>(json, object: TypeToken<T>() {}.type)
