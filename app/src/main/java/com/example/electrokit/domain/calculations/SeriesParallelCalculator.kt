package com.example.electrokit.domain.calculations

import kotlin.math.pow
import kotlin.math.roundToInt

data class SeriesParallelResult(
    val seriesTotal: Double,
    val parallelTotal: Double
)

object SeriesParallelCalculator {

    fun calculateResistors(values: List<Double>): SeriesParallelResult? {
        val validValues = values.filter { it > 0 }
        if (validValues.isEmpty()) return null

        val series = validValues.sum()
        val reciprocalSum = validValues.sumOf { 1.0 / it }
        val parallel = if (reciprocalSum > 0) 1.0 / reciprocalSum else 0.0

        return SeriesParallelResult(
            seriesTotal = roundToDecimals(series, 4),
            parallelTotal = roundToDecimals(parallel, 4)
        )
    }

    fun calculateCapacitors(values: List<Double>): SeriesParallelResult? {
        val validValues = values.filter { it > 0 }
        if (validValues.isEmpty()) return null

        // Capacitors in parallel ADD UP; in series reciprocals add up
        val parallel = validValues.sum()
        val reciprocalSum = validValues.sumOf { 1.0 / it }
        val series = if (reciprocalSum > 0) 1.0 / reciprocalSum else 0.0

        return SeriesParallelResult(
            seriesTotal = roundToDecimals(series, 4),
            parallelTotal = roundToDecimals(parallel, 4)
        )
    }

    fun calculateInductors(values: List<Double>): SeriesParallelResult? {
        // Inductors follow same rules as resistors (assuming no mutual coupling)
        return calculateResistors(values)
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (value * factor).roundToInt() / factor
    }
}
