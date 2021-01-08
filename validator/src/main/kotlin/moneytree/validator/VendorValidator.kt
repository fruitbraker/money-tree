package moneytree.validator

import moneytree.domain.Vendor
import moneytree.domain.validation.schema.VendorValidationSchema

class VendorValidator : Validator<Vendor> {
    override fun validate(input: Vendor): ValidationResult {
        input.id?.let { uuid ->
            if (uuid.validateUUID() is ValidationResult.Rejected) return ValidationResult.Rejected
        }

        if (input.name.length > VendorValidationSchema.NAME_LENGTH) return ValidationResult.Rejected

        return ValidationResult.Accepted
    }
}
