package moneytree.domain.vendor

import moneytree.domain.metadata.Metadata
import java.util.*

data class Vendor(
    val id: UUID?,
    val name: String,
    val metadata: Metadata
)