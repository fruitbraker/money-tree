package moneytree.domain.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.math.BigDecimal
import java.util.UUID
import moneytree.libs.commons.serde.TrimStringDeserializer

data class ExpenseCategory(
    val id: UUID?,
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val name: String,
    val targetAmount: BigDecimal
)
