package com.example.electrokit.domain.calculations

import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

data class LedResistorResult(
    val calculatedResistance: Double, // in Ohms
    val standardResistance: Double,   // nearest E24 standard resistor value
    val minimumPowerRating: Double,   // Watts (e.g., 0.25W, 0.5W, 1W)
    val actualPowerDissipation: Double // Watts
)

object LedResistorCalculator {

    // E24 Standard Resistor Values multiplier bases
    private val E24_BASE = listOf(
        1.0, 1.1, 1.2, 1.3, 1.5, 1.6, 1.8, 2.0, 2.2, 2.4, 2.7, 3.0,
        3.3, 3.6, 3.9, 4.3, 4.7, 5.1, 5.6, 6.2, 6.8, 7.5, 8.2, 9.1
    )

    fun calculate(
        supplyVoltage: Double,
        ledForwardVoltage: Double,
        ledCurrentmA: Double,
        ledCount: Int = 1
    ): LedResistorResult? {
        if (supplyVoltage <= 0 || ledForwardVoltage <= 0 || ledCurrentmA <= 0 || ledCount <= 0) return null
        
        val totalLedVf = ledForwardVoltage * ledCount
        if (supplyVoltage <= totalLedVf) return null // Supply must exceed total LED forward voltage

        val currentA = ledCurrentmA / 1000.0
        val vResistor = supplyVoltage - totalLedVf
        val rExact = vResistor / currentA
        val powerExact = vResistor * currentA

        val rStandard = findNearestE24(rExact)
        val minPower = calculateMinPowerRating(powerExact)

        return LedResistorResult(
            calculatedResistance = roundToDecimals(rExact, 2),
            standardResistance = rStandard,
            minimumPowerRating = minPower,
            actualPowerDissipation = roundToDecimals(powerExact, 3)
        )
    }

    private fun findNearestE24(value: Double): Double {
        if (value <= 0) return 0.0
        val exponent = kotlin.math.floor(kotlin.math.log10(value))
        val normalized = value / 10.0.pow(exponent)
        
        var nearestBase = E24_BASE[0]
        var minDiff = kotlin.math.abs(normalized - nearestBase)

        for (base in E24_BASE) {
            val diff = kotlin.math.abs(normalized - base)
            if (diff < minDiff) {
                minDiff = diff
                nearestBase = base
            }
        }

        val result = nearestBase * 10.0.pow(exponent)
        return roundToDecimals(result, 2)
    }

    private fun calculateMinPowerRating(powerWatts: Double): Double {
        val safePower = powerWatts * 2.0 // 100% safety margin standard engineering practice
        return when {
            safePower <= 0.125 -> 0.125 // 1/8 Watt
            safePower <= 0.25 -> 0.25   // 1/4 Watt
            safePower <= 0.5 -> 0.5     // 1/2 Watt
            safePower <= 1.0 -> 1.0     // 1 Watt
            safePower <= 2.0 -> 2.0     // 2 Watt
            else -> ceil(safePower)     // N Watts
        }
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (value * factor).roundToInt() / factor
    }
}
