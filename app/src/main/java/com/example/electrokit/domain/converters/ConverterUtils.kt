package com.example.electrokit.domain.converters

import java.math.BigInteger

enum class NumberSystem(val displayName: String, val radix: Int) {
    DECIMAL("Decimal", 10),
    BINARY("Binary", 2),
    OCTAL("Octal", 8),
    HEXADECIMAL("Hexadecimal", 16);

    companion object {
        fun fromDisplayName(name: String): NumberSystem {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: DECIMAL
        }
    }
}

object ConverterUtils {

    /**
     * Validates input against the selected NumberSystem's allowed character set.
     * @return null if valid, or an error string if invalid.
     */
    fun validateInput(input: String, system: NumberSystem): String? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return "Please enter a number to convert."
        }

        val pattern = when (system) {
            NumberSystem.BINARY -> Regex("^[01]+$")
            NumberSystem.OCTAL -> Regex("^[0-7]+$")
            NumberSystem.DECIMAL -> Regex("^[0-9]+$")
            NumberSystem.HEXADECIMAL -> Regex("^[0-9a-fA-F]+$")
        }

        if (!trimmed.matches(pattern)) {
            return when (system) {
                NumberSystem.BINARY -> "Invalid binary number. Only digits 0 and 1 are allowed."
                NumberSystem.OCTAL -> "Invalid octal number. Only digits 0-7 are allowed."
                NumberSystem.DECIMAL -> "Invalid decimal number. Only digits 0-9 are allowed."
                NumberSystem.HEXADECIMAL -> "Invalid hexadecimal number. Only 0-9 and A-F are allowed."
            }
        }
        return null
    }

    fun decimalToBinary(decimalStr: String): String =
        BigInteger(decimalStr.trim(), 10).toString(2).uppercase()

    fun binaryToDecimal(binaryStr: String): String =
        BigInteger(binaryStr.trim(), 2).toString(10)

    fun decimalToOctal(decimalStr: String): String =
        BigInteger(decimalStr.trim(), 10).toString(8)

    fun octalToDecimal(octalStr: String): String =
        BigInteger(octalStr.trim(), 8).toString(10)

    fun decimalToHex(decimalStr: String): String =
        BigInteger(decimalStr.trim(), 10).toString(16).uppercase()

    fun hexToDecimal(hexStr: String): String =
        BigInteger(hexStr.trim(), 16).toString(10)

    /**
     * Generic Converter: Converts from any base [fromSystem] to [toSystem]
     */
    fun convert(input: String, fromSystem: NumberSystem, toSystem: NumberSystem): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""
        if (fromSystem == toSystem) {
            return trimmed.uppercase()
        }

        val decimalValue = BigInteger(trimmed, fromSystem.radix)

        return when (toSystem) {
            NumberSystem.DECIMAL -> decimalValue.toString(10)
            NumberSystem.BINARY -> decimalValue.toString(2).uppercase()
            NumberSystem.OCTAL -> decimalValue.toString(8)
            NumberSystem.HEXADECIMAL -> decimalValue.toString(16).uppercase()
        }
    }
}
