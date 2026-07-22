package com.example.electrokit.domain.converters

data class NumberSystemResult(
    val decimal: String,
    val binary: String,
    val octal: String,
    val hex: String,
    val isValid: Boolean,
    val errorMessage: String? = null
)

object NumberSystemConverter {

    fun convertFromDecimal(input: String): NumberSystemResult {
        val clean = input.trim()
        if (clean.isEmpty()) return emptyResult()
        return try {
            val num = clean.toLong()
            NumberSystemResult(
                decimal = num.toString(),
                binary = num.toString(2).uppercase(),
                octal = num.toString(8).uppercase(),
                hex = num.toString(16).uppercase(),
                isValid = true
            )
        } catch (e: Exception) {
            NumberSystemResult(clean, "", "", "", false, "Invalid Decimal input")
        }
    }

    fun convertFromBinary(input: String): NumberSystemResult {
        val clean = input.trim()
        if (clean.isEmpty()) return emptyResult()
        return try {
            val num = clean.toLong(2)
            NumberSystemResult(
                decimal = num.toString(),
                binary = clean.uppercase(),
                octal = num.toString(8).uppercase(),
                hex = num.toString(16).uppercase(),
                isValid = true
            )
        } catch (e: Exception) {
            NumberSystemResult("", clean, "", "", false, "Invalid Binary input (0 & 1 only)")
        }
    }

    fun convertFromOctal(input: String): NumberSystemResult {
        val clean = input.trim()
        if (clean.isEmpty()) return emptyResult()
        return try {
            val num = clean.toLong(8)
            NumberSystemResult(
                decimal = num.toString(),
                binary = num.toString(2).uppercase(),
                octal = clean.uppercase(),
                hex = num.toString(16).uppercase(),
                isValid = true
            )
        } catch (e: Exception) {
            NumberSystemResult("", "", clean, "", false, "Invalid Octal input (digits 0-7 only)")
        }
    }

    fun convertFromHex(input: String): NumberSystemResult {
        val clean = input.trim()
        if (clean.isEmpty()) return emptyResult()
        return try {
            val num = clean.toLong(16)
            NumberSystemResult(
                decimal = num.toString(),
                binary = num.toString(2).uppercase(),
                octal = num.toString(8).uppercase(),
                hex = clean.uppercase(),
                isValid = true
            )
        } catch (e: Exception) {
            NumberSystemResult("", "", "", clean, false, "Invalid Hex input (digits 0-9 & A-F)")
        }
    }

    private fun emptyResult() = NumberSystemResult("", "", "", "", true)
}
