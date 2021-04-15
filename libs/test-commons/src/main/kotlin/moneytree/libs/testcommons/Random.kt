package moneytree.libs.testcommons

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.random.Random

private val random = Random(System.currentTimeMillis())

// For random string
private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

fun randomString(length: Int = 5) =
    (1..length)
        .map { charPool.random() }
        .joinToString("")

fun randomBigDecimal(): BigDecimal =
    BigDecimal(randomDouble()).setScale(4, RoundingMode.HALF_UP)

fun randomDouble(): Double = random.nextDouble(0.000, 99999999.9999)

fun randomLong(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = random.nextLong(min, max)

fun randomLocalDate(): LocalDate = LocalDate.now().plusDays(randomLong(0, 1000))
