package com.example.electrokit.domain.calculations

import kotlin.math.pow
import kotlin.math.roundToInt

data class OhmsLawResult(
    val voltage: Double,
    val current: Double,
    val resistance: Double,
    val power: Double
)

object OhmsLawCalculator {

    fun calculate(
        v: Double? = null,
        i: Double? = null,
        r: Double? = null,
        p: Double? = null
    ): OhmsLawResult? {
        try {
            var calcV = v
            var calcI = i
            var calcR = r
            var calcP = p

            if (calcV != null && calcI != null && calcI != 0.0) {
                calcR = calcV / calcI
                calcP = calcV * calcI
            } else if (calcV != null && calcR != null && calcR != 0.0) {
                calcI = calcV / calcR
                calcP = (calcV * calcV) / calcR
            } else if (calcI != null && calcR != null) {
                calcV = calcI * calcR
                calcP = calcI * calcI * calcR
            } else if (calcP != null && calcV != null && calcV != 0.0) {
                calcI = calcP / calcV
                calcR = (calcV * calcV) / calcP
            } else if (calcP != null && calcI != null && calcI != 0.0) {
                calcV = calcP / calcI
                calcR = calcP / (calcI * calcI)
            } else if (calcP != null && calcR != null && calcR > 0.0) {
                calcV = kotlin.math.sqrt(calcP * calcR)
                calcI = kotlin.math.sqrt(calcP / calcR)
            } else {
                return null
            }

            return OhmsLawResult(
                voltage = roundToDecimals(calcV ?: 0.0, 4),
                current = roundToDecimals(calcI ?: 0.0, 6),
                resistance = roundToDecimals(calcR ?: 0.0, 4),
                power = roundToDecimals(calcP ?: 0.0, 4)
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (value * factor).roundToInt() / factor
    }
}
