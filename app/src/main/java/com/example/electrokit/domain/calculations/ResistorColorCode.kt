package com.example.electrokit.domain.calculations

enum class ResistorColor(val colorName: String, val digit: Int, val multiplier: Double, val tolerance: Double?, val hex: String) {
    BLACK("Black", 0, 1.0, null, "#000000"),
    BROWN("Brown", 1, 10.0, 1.0, "#8B4513"),
    RED("Red", 2, 100.0, 2.0, "#FF0000"),
    ORANGE("Orange", 3, 1000.0, null, "#FFA500"),
    YELLOW("Yellow", 4, 10000.0, null, "#FFFF00"),
    GREEN("Green", 5, 100000.0, 0.5, "#008000"),
    BLUE("Blue", 6, 1000000.0, 0.25, "#0000FF"),
    VIOLET("Violet", 7, 10000000.0, 0.10, "#8A2BE2"),
    GREY("Grey", 8, 100000000.0, 0.05, "#808080"),
    WHITE("White", 9, 1000000000.0, null, "#FFFFFF"),
    GOLD("Gold", -1, 0.1, 5.0, "#D4AF37"),
    SILVER("Silver", -2, 0.01, 10.0, "#C0C0C0")
}

data class ResistorColorResult(
    val resistanceOhms: Double,
    val formattedResistance: String,
    val tolerancePercent: Double,
    val minResistance: Double,
    val maxResistance: Double
)

object ResistorColorCode {

    fun decode4Band(
        band1: ResistorColor,
        band2: ResistorColor,
        multiplier: ResistorColor,
        tolerance: ResistorColor
    ): ResistorColorResult {
        val baseValue = (band1.digit * 10) + band2.digit
        val ohms = baseValue * multiplier.multiplier
        val tol = tolerance.tolerance ?: 5.0
        val minR = ohms * (1 - tol / 100.0)
        val maxR = ohms * (1 + tol / 100.0)

        return ResistorColorResult(
            resistanceOhms = ohms,
            formattedResistance = formatResistance(ohms),
            tolerancePercent = tol,
            minResistance = minR,
            maxResistance = maxR
        )
    }

    fun decode5Band(
        band1: ResistorColor,
        band2: ResistorColor,
        band3: ResistorColor,
        multiplier: ResistorColor,
        tolerance: ResistorColor
    ): ResistorColorResult {
        val baseValue = (band1.digit * 100) + (band2.digit * 10) + band3.digit
        val ohms = baseValue * multiplier.multiplier
        val tol = tolerance.tolerance ?: 1.0
        val minR = ohms * (1 - tol / 100.0)
        val maxR = ohms * (1 + tol / 100.0)

        return ResistorColorResult(
            resistanceOhms = ohms,
            formattedResistance = formatResistance(ohms),
            tolerancePercent = tol,
            minResistance = minR,
            maxResistance = maxR
        )
    }

    fun formatResistance(ohms: Double): String {
        return when {
            ohms >= 1_000_000_000 -> String.format("%.2f GΩ", ohms / 1_000_000_000.0)
            ohms >= 1_000_000 -> String.format("%.2f MΩ", ohms / 1_000_000.0)
            ohms >= 1_000 -> String.format("%.2f kΩ", ohms / 1_000.0)
            else -> String.format("%.2f Ω", ohms)
        }.replace(".00", "")
    }
}
