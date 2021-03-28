import java.time.LocalDate
import java.util.UUID

fun List<String?>.toUUIDList(): List<UUID> = this.mapNotNull { UUID.fromString(it) }

fun String.toLocalDate(): LocalDate = LocalDate.parse(this)
