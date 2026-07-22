# Complete Integration & Architecture Guide - NumberSystemConverter

This document provides a comprehensive technical breakdown of the **NumberSystemConverter** app, specifically formatted so you can easily copy and integrate its conversion engine, validation rules, resources, and UI components into any existing or new Android project.

---

## 1. Core Logic Engine (`ConverterUtils.kt`)

The entire conversion engine is completely decoupled from the UI. You can copy the code below into your destination project package.

### File: `ConverterUtils.kt`
[ConverterUtils.kt](file:///c:/MyProject/Mycreate/number%20system/app/src/main/java/com/example/numbersystemconverter/ConverterUtils.kt)

```kotlin
package com.example.numbersystemconverter

import java.math.BigInteger

/**
 * Enum defining the 4 number systems and their base radix.
 */
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

    // --- Individual Conversion Methods ---
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
        if (fromSystem == toSystem) {
            return trimmed.uppercase()
        }

        // Convert input -> Decimal BigInteger intermediate
        val decimalValue = BigInteger(trimmed, fromSystem.radix)

        // Convert Decimal BigInteger intermediate -> Target radix string
        return when (toSystem) {
            NumberSystem.DECIMAL -> decimalValue.toString(10)
            NumberSystem.BINARY -> decimalValue.toString(2).uppercase()
            NumberSystem.OCTAL -> decimalValue.toString(8)
            NumberSystem.HEXADECIMAL -> decimalValue.toString(16).uppercase()
        }
    }
}
```

---

## 2. Integration into Another App (3 Simple Steps)

### Step 1: Copy `ConverterUtils.kt` & `NumberSystem` Enum
Place `ConverterUtils.kt` anywhere inside your target app source directory (e.g. `com.yourcompany.yourapp.utils`).

### Step 2: Call Validation & Conversion Logic
In your target Activity / Fragment / ViewModel:

```kotlin
val inputNumber = "110101" // User input
val fromSystem = NumberSystem.BINARY
val toSystem = NumberSystem.HEXADECIMAL

// 1. Validate Input
val error = ConverterUtils.validateInput(inputNumber, fromSystem)
if (error != null) {
    // Show error in UI (Toast, TextView, or TextInputLayout)
    println("Error: $error")
} else {
    // 2. Perform Conversion
    val result = ConverterUtils.convert(inputNumber, fromSystem, toSystem)
    println("Converted Result: $result") // Output: "35"
}
```

---

## 3. UI Controller Breakdown (`MainActivity.kt`)

[MainActivity.kt](file:///c:/MyProject/Mycreate/number%20system/app/src/main/java/com/example/numbersystemconverter/MainActivity.kt) demonstrates how to bind controls:

- **Dropdown (Spinner) Setup**:
  Uses `ArrayAdapter` loaded from `R.array.number_systems` (`[Decimal, Binary, Octal, Hexadecimal]`).
- **Dynamic Hints**:
  Updates input hint when `spinnerFrom` selection changes (`Decimal 0-9`, `Binary 0-1`, `Octal 0-7`, `Hex 0-9, A-F`).
- **Swap Feature**:
  Exchanges position of `spinnerFrom` and `spinnerTo` and instantly updates the result if input is present.
- **Copy Feature**:
  Copies `tvResult` text to Android `ClipboardManager`.

---

## 4. Resource Dependencies

### `arrays.xml`
[arrays.xml](file:///c:/MyProject/Mycreate/number%20system/app/src/main/res/values/arrays.xml)
```xml
<resources>
    <string-array name="number_systems">
        <item>Decimal</item>
        <item>Binary</item>
        <item>Octal</item>
        <item>Hexadecimal</item>
    </string-array>
</resources>
```
