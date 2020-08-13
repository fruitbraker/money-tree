package moneytree.domain

@Suppress("FunctionName", "unused")
inline fun <T> Try(operation: () -> T): Result<T, Throwable> {
    return try {
        Result.Ok(operation())
    } catch (e: Throwable) {
        Result.Err(e)
    }
}

@Suppress("EqualsOrHashCode")
sealed class Result<out T, out E> {
    class Ok<out T>(val value: T) : Result<T, Nothing>()
    class Err<out E>(val error: E) : Result<Nothing, E>()

    override fun equals(other: Any?): Boolean {
        return when (this) {
            is Ok -> other is Ok<*> && this.value == other.value
            is Err -> other is Err<*> && this.error == other.error
        }
    }
}

// NOTES:
// Use `when` when you are only doing side-effects with no further chaining
// Use `map/mapErr` when you are transforming the Ok and Err values into different Ok and Err values and intend to further chain/process those different values
// Use `onOk/onErr` when you are side-effecting and doing further chaining

@Suppress("unused")
inline fun <T, U, E> Result<T, E>.map(transform: (T) -> U): Result<U, E> {
    return when (this) {
        is Result.Ok -> Result.Ok(transform(value))
        is Result.Err -> this
    }
}

@Suppress("unused")
inline fun <T, E, F> Result<T, E>.mapErr(transform: (E) -> F): Result<T, F> {
    return when (this) {
        is Result.Ok -> this
        is Result.Err -> Result.Err(transform(error))
    }
}

@Suppress("unused")
inline fun <T, U, E> Result<T, E>.flatMap(transform: (T) -> Result<U, E>): Result<U, E> {
    return when (this) {
        is Result.Ok -> transform(value)
        is Result.Err -> this
    }
}

@Suppress("unused")
inline fun <T, E, F> Result<T, E>.flatMapErr(transform: (E) -> Result<T, F>): Result<T, F> {
    return when (this) {
        is Result.Ok -> this
        is Result.Err -> transform(error)
    }
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T {
    return when (this) {
        is Result.Ok -> value
        is Result.Err -> default(error)
    }
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.onOk(operation: (T) -> Unit): Result<T, E> {
    if (this is Result.Ok) {
        operation(value)
    }

    return this
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.onErr(operation: (E) -> Unit): Result<T, E> {
    if (this is Result.Err) {
        operation(error)
    }

    return this
}

@Suppress("unused")
fun <T, E> Result<T, E>.or(other: Result<T, E>): Result<T, E> {
    return when (this) {
        is Result.Ok -> this
        is Result.Err -> other
    }
}

@Suppress("unused")
inline fun <T, U, E> U.fold(operand: Result<T, E>, op: (U, T) -> U): U {
    return when (operand) {
        is Result.Ok -> op(this, operand.value)
        is Result.Err -> this
    }
}

@Suppress("unused")
inline fun <T, E> Result<T, E>.otherwise(then: (err: Result.Err<E>) -> T): T {
    return when (this) {
        is Result.Ok -> value
        is Result.Err -> then(this)
    }
}

fun <T> T.toResult() = this.toOk()
fun <T> T.toOk() = Result.Ok(this)
fun <E> E.toErr() = Result.Err(this)
