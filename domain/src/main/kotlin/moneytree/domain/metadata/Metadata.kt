package moneytree.domain.metadata

import java.time.OffsetDateTime

data class Metadata(
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val notes: String?
)
