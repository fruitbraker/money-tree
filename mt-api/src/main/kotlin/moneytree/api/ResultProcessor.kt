package moneytree.api

import moneytree.libs.commons.result.Result
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens

fun <T, E> processResult(result: Result<T?, E>, lens: BiDiBodyLens<T>): Response {
    return when (result) {
        is Result.Ok -> {
            result.value?.let {
                Response(Status.OK).with(lens of it)
            } ?: Response(Status.NOT_FOUND)
        }
        is Result.Err -> Response(Status.BAD_REQUEST)
    }
}
