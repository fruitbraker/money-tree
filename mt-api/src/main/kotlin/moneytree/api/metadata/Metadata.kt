package moneytree.api.metadata

import java.time.LocalDate

data class Metadata(
    val dateCreated: LocalDate?,
    val dateModified: LocalDate?,
    val notes: String?
)
