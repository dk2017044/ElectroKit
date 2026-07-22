package com.example.electrokit.domain.calculations

import kotlin.math.pow

data class CapacitorCodeResult(
    val code: String,
    val picofarads: Double,
    val nanofarads: Double,
    val microfarads: Double,
    val formattedCapacitance: String,
    val tolerance: String
)

object CapacitorCode {

    private val TOLERANCE_CODES = mapOf(
        'B' to "±0.1 pF", 'C' to "±0.25 pF", 'D' to "±0.5 pF",
        'F' to "±1%", 'G' to "±2%", 'J' to "±5%", 'K' to "±10%", 'M' to "±20%",
        'Z' to "+80% / -20%"
    )

    fun decode(codeRaw: String): CapacitorCodeResult? {
        val code = codeRaw.trim().uppercase()
        if (code.isEmpty()) return null

        try {
            var numberPart = ""
            var letterPart: Char? = null

            for (char in code) {
                if (char.isDigit()) {
                    numberPart += char
                } else if (char.isLetter()) {
                    letterPart = char
                    break
                }
            }

            if (numberPart.length < 2) return null

            val pf: Double = if (numberPart.length == 2) {
                numberPart.toDouble()
            } else {
                val digits = numberPart.substring(0, 2).toDouble()
                val multiplier = 10.0.pow(numberPart[2].digitToInt().toDouble())
                digits * multiplier
            }

            val nf = pf / 1_000.0
            val uf = pf / 1_000_000.0

            val formatted = when {
                uf >= 1.0 -> String.format("%.2f µF", uf)
                nf >= 1.0 -> String.format("%.2f nF", nf)
                else -> String.format("%.0f pF", pf)
            }.replace(".00", "")

            val tolText = letterPart?.let { TOLERANCE_CODES[it] } ?: "Not Specified"

            return CapacitorCodeResult(
                code = code,
                picofarads = pf,
                nanofarads = nf,
                microfarads = uf,
                formattedCapacitance = formatted,
                tolerance = tolText
            )
        } catch (e: Exception) {
            return null
        }
    }
}
