package moneytree.libs.commons.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val jackson: ObjectMapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule().addDeserializer(String::class.java, TrimStringDeserializer))
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

fun Any.toJson(): String = jackson.writeValueAsString(this)

object TrimStringDeserializer : JsonDeserializer<String>() {
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): String {
        return jsonParser.valueAsString.trim()
    }
}
