package moneytree.validator

import moneytree.domain.entity.IncomeCategory
import moneytree.domain.validation.schema.IncomeCategoryValidationSchema

class IncomeCategoryValidator : Validator<IncomeCategory> {
    override fun validate(input: IncomeCategory): ValidationResult {
        input.incomeCategoryId?.let { uuid ->
            if (uuid.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        }

        if (input.name.length > IncomeCategoryValidationSchema.NAME_LENGTH) return ValidationResult.Rejected

        return ValidationResult.Accepted
    }
}
