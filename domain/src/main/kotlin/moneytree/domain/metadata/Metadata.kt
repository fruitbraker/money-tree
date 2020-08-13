package moneytree.domain.metadata

import java.time.OffsetDateTime

data class Metadata(
    val notes: String?,
    val dateCreated: OffsetDateTime?,
    val dateModified: OffsetDateTime?
)
