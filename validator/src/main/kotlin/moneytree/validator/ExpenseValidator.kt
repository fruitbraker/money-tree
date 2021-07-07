package moneytree.validator

import moneytree.domain.entity.Expense
import moneytree.domain.validation.schema.ExpenseValidationSchema

class ExpenseValidator : Validator<Expense> {
    override fun validate(input: Expense): ValidationResult {
        input.id?.let { uuid ->
            if (uuid.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        }

        if (input.vendorId.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        if (input.expenseCategoryId.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected

        if (input.transactionAmount < ExpenseValidationSchema.EXPENSE_AMOUNT_MIN ||
            input.transactionAmount > ExpenseValidationSchema.EXPENSE_AMOUNT_MAX
        ) return ValidationResult.Rejected

        if (input.notes.length > ExpenseValidationSchema.EXPENSE_NOTES_MAX_LENGTH) return ValidationResult.Rejected

        return ValidationResult.Accepted
    }
}
