package moneytree.libs.test.common

// For random string
private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

fun randomString(length: Int = 5) =
    (1..length)
        .map { charPool.random() }
        .joinToString("")
