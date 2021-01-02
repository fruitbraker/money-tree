package moneytree.validator

import moneytree.domain.ExpenseCategory
import moneytree.domain.validation.schema.ExpenseCategoryValidationSchema

class ExpenseCategoryValidator : Validator<ExpenseCategory> {
    override fun validate(input: ExpenseCategory): ValidationResult {
        var isValid = true

        if (input.name.length > ExpenseCategoryValidationSchema.NAME_LENGTH)
            isValid = false
        if (input.targetAmount < ExpenseCategoryValidationSchema.TARGET_AMOUNT_MIN ||
            input.targetAmount > ExpenseCategoryValidationSchema.TARGET_AMOUNT_MAX
        )
            isValid = false

        return if (isValid) ValidationResult.Accepted else ValidationResult.Rejected
    }
}
