package moneytree.api

import moneytree.libs.commons.result.Result
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens

fun <T, E> processGetResult(result: Result<List<T>, E>, lens: BiDiBodyLens<List<T>>): Response {
    return when (result) {
        is Result.Ok -> Response(Status.OK).with(lens of result.value)
        is Result.Err -> Response(Status.BAD_REQUEST)
    }
}

fun <T, E> processGetByIdResult(result: Result<T?, E>, lens: BiDiBodyLens<T>): Response {
    return when (result) {
        is Result.Ok -> {
            result.value?.let {
                Response(Status.OK).with(lens of it)
            } ?: Response(Status.NOT_FOUND)
        }
        is Result.Err -> Response(Status.BAD_REQUEST)
    }
}

fun <T, E> processInsertResult(result: Result<T, E>, lens: BiDiBodyLens<T>): Response {
    return when (result) {
        is Result.Ok -> Response(Status.CREATED).with(lens of result.value)
        is Result.Err -> Response(Status.BAD_REQUEST)
    }
}
