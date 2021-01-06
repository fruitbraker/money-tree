package moneytree.validator

import moneytree.domain.ExpenseCategory
import moneytree.domain.validation.schema.ExpenseCategoryValidationSchema

class ExpenseCategoryValidator : Validator<ExpenseCategory> {
    override fun validate(input: ExpenseCategory): ValidationResult {
        input.id?.let { uuid ->
            if (uuid.toString().validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        }

        if (input.name.length > ExpenseCategoryValidationSchema.NAME_LENGTH) return ValidationResult.Rejected
        if (input.targetAmount < ExpenseCategoryValidationSchema.TARGET_AMOUNT_MIN ||
            input.targetAmount > ExpenseCategoryValidationSchema.TARGET_AMOUNT_MAX
        ) return ValidationResult.Rejected

        return ValidationResult.Accepted
    }
}
