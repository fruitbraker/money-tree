package moneytree.libs.serde

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val jackson: ObjectMapper = jacksonObjectMapper()
    .registerModule(Jdk8Module())
    .registerModule(JavaTimeModule())
    .registerModule(KotlinModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

fun Any.toJson(): String = jackson.writeValueAsString(this)
