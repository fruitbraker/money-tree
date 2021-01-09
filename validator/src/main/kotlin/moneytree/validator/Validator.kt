package moneytree.validator

import java.util.UUID

private val UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex()

interface Validator<T> {
    fun validate(input: T): ValidationResult
    fun validateWithUUID(input: T, uuid: String): ValidationResult {
        return when (uuid.validateUUID()) {
            ValidationResult.Accepted -> validate(input)
            ValidationResult.Rejected -> ValidationResult.Rejected
        }
    }
}

fun String.validateUUID(): ValidationResult {
    return if (this.matches(UUID_REGEX)) ValidationResult.Accepted
    else ValidationResult.Rejected
}

fun UUID.validateUUID(): ValidationResult {
    return this.toString().validateUUID()
}

sealed class ValidationResult {
    object Accepted : ValidationResult()
    object Rejected : ValidationResult()
}
