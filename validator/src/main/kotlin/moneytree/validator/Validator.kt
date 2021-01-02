package moneytree.validator

interface Validator<T> {
    fun validate(input: T): ValidationResult
}

sealed class ValidationResult {
    object Accepted : ValidationResult()
    object Rejected : ValidationResult()
}
