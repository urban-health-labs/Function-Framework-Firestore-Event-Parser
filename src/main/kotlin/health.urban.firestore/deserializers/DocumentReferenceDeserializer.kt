package ai.rever.meghsamaaroh.helper.deserializers

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class DocumentReferenceDeserializer: JsonDeserializer<DocumentReference> {
  private val FIRESTORE: Firestore = FirestoreOptions.getDefaultInstance().service

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DocumentReference {
    return FIRESTORE.document(json?.asString!!.split("/(default)/documents/")[1])
  }
}