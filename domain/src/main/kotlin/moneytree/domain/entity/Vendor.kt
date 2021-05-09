package moneytree.domain.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.UUID
import moneytree.libs.commons.serde.TrimStringDeserializer

data class Vendor(
    val id: UUID?,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val name: String
)
