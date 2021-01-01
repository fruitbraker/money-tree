package moneytree.libs.test.commons

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

private val random = Random(System.currentTimeMillis())

// For random string
private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

fun randomString(length: Int = 5) =
    (1..length)
        .map { charPool.random() }
        .joinToString("")

fun randomBigDecimal() =
    BigDecimal(randomDouble()).setScale(4, RoundingMode.HALF_UP)

fun randomDouble() = random.nextDouble(0.000, 99999999.9999)
