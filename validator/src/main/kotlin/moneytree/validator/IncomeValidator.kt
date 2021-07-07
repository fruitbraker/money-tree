package moneytree.validator

import moneytree.domain.entity.Income
import moneytree.domain.validation.schema.IncomeValidationSchema

class IncomeValidator : Validator<Income> {
    override fun validate(input: Income): ValidationResult {
        input.incomeId?.let { uuid ->
            if (uuid.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        }

        if (input.incomeCategoryId.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected

        if (input.transactionAmount < IncomeValidationSchema.INCOME_AMOUNT_MIN ||
            input.transactionAmount > IncomeValidationSchema.INCOME_AMOUNT_MAX
        ) return ValidationResult.Rejected

        if (input.notes.length > IncomeValidationSchema.INCOME_NOTES_MAX_LENGTH) return ValidationResult.Rejected
        if (input.source.length > IncomeValidationSchema.INCOME_SOURCE_MAX_LENGTH) return ValidationResult.Rejected

        return ValidationResult.Accepted
    }
}
