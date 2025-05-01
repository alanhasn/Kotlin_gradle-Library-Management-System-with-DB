package data

// Validation class to hold error messages
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
