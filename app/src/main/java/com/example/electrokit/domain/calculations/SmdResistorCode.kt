package com.example.electrokit.domain.calculations

import kotlin.math.pow

data class SmdResult(
    val code: String,
    val resistanceOhms: Double,
    val formattedResistance: String,
    val tolerance: String
)

object SmdResistorCode {

    // EIA-96 lookup table for 1% SMD resistors
    private val EIA96_CODES = mapOf(
        "01" to 100, "02" to 102, "03" to 105, "04" to 107, "05" to 110, "06" to 113, "07" to 115, "08" to 118,
        "09" to 121, "10" to 124, "11" to 127, "12" to 130, "13" to 133, "14" to 137, "15" to 140, "16" to 143,
        "17" to 147, "18" to 150, "19" to 154, "20" to 158, "21" to 162, "22" to 165, "23" to 169, "24" to 174,
        "25" to 178, "26" to 182, "27" to 187, "28" to 191, "29" to 196, "30" to 200, "31" to 205, "32" to 210,
        "33" to 215, "34" to 221, "35" to 226, "36" to 232, "37" to 237, "38" to 243, "39" to 249, "40" to 255,
        "41" to 261, "42" to 267, "43" to 274, "44" to 280, "45" to 287, "46" to 294, "47" to 301, "48" to 309,
        "49" to 316, "50" to 324, "51" to 332, "52" to 340, "53" to 348, "54" to 357, "55" to 365, "56" to 374,
        "57" to 383, "58" to 392, "59" to 402, "60" to 412, "61" to 422, "62" to 432, "63" to 442, "64" to 453,
        "65" to 464, "66" to 475, "67" to 487, "68" to 499, "69" to 511, "70" to 523, "71" to 536, "72" to 549,
        "73" to 562, "74" to 576, "75" to 590, "76" to 604, "77" to 619, "78" to 634, "79" to 649, "80" to 665,
        "81" to 681, "82" to 698, "83" to 715, "84" to 732, "85" to 750, "86" to 768, "87" to 787, "88" to 806,
        "89" to 825, "90" to 845, "91" to 866, "92" to 887, "93" to 909, "94" to 931, "95" to 953, "96" to 976
    )

    private val EIA96_MULTIPLIERS = mapOf(
        'Z' to 0.001, 'Y' to 0.01, 'X' to 0.1, 'A' to 1.0, 'B' to 10.0,
        'C' to 100.0, 'D' to 1000.0, 'E' to 10000.0, 'F' to 100000.0
    )

    fun decode(codeRaw: String): SmdResult? {
        val code = codeRaw.trim().uppercase()
        if (code.isEmpty()) return null

        try {
            // Case 1: Decimal 'R' notation (e.g. 4R7 = 4.7Ω, R10 = 0.10Ω, 0R22 = 0.22Ω)
            if (code.contains('R')) {
                val valueStr = code.replace('R', '.')
                val ohms = valueStr.toDouble()
                return SmdResult(code, ohms, ResistorColorCode.formatResistance(ohms), "5%")
            }

            // Case 2: 3-Digit Code (5% tolerance) e.g., 472 = 47 * 10^2 = 4700Ω = 4.7kΩ
            if (code.length == 3 && code.all { it.isDigit() }) {
                val digits = code.substring(0, 2).toDouble()
                val multiplier = 10.0.pow(code[2].digitToInt().toDouble())
                val ohms = digits * multiplier
                return SmdResult(code, ohms, ResistorColorCode.formatResistance(ohms), "5%")
            }

            // Case 3: 4-Digit Code (1% tolerance) e.g., 1002 = 100 * 10^2 = 10,000Ω = 10kΩ
            if (code.length == 4 && code.all { it.isDigit() }) {
                val digits = code.substring(0, 3).toDouble()
                val multiplier = 10.0.pow(code[3].digitToInt().toDouble())
                val ohms = digits * multiplier
                return SmdResult(code, ohms, ResistorColorCode.formatResistance(ohms), "1%")
            }

            // Case 4: EIA-96 3-character code (1% precision) e.g., 01A = 100 * 1 = 100Ω
            if (code.length == 3 && EIA96_CODES.containsKey(code.substring(0, 2)) && EIA96_MULTIPLIERS.containsKey(code[2])) {
                val base = EIA96_CODES[code.substring(0, 2)]!!
                val mult = EIA96_MULTIPLIERS[code[2]]!!
                val ohms = base * mult
                return SmdResult(code, ohms, ResistorColorCode.formatResistance(ohms), "1% (EIA-96)")
            }

            return null
        } catch (e: Exception) {
            return null
        }
    }
}
