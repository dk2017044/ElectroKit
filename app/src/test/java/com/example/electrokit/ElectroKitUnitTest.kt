package com.example.electrokit

import com.example.electrokit.domain.calculations.*
import com.example.electrokit.domain.converters.NumberSystemConverter
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ElectroKitUnitTest {

    @Test
    fun testOhmsLawCalculator() {
        val res = OhmsLawCalculator.calculate(v = 12.0, r = 24.0)
        assertNotNull(res)
        assertEquals(0.5, res!!.current, 0.001)
        assertEquals(6.0, res.power, 0.001)
    }

    @Test
    fun testLedResistorCalculator() {
        val res = LedResistorCalculator.calculate(supplyVoltage = 9.0, ledForwardVoltage = 2.0, ledCurrentmA = 20.0)
        assertNotNull(res)
        assertEquals(350.0, res!!.calculatedResistance, 0.1)
        assertEquals(360.0, res.standardResistance, 0.1)
        assertEquals(0.5, res.minimumPowerRating, 0.01)
    }

    @Test
    fun testSeriesParallelCalculator() {
        val rResult = SeriesParallelCalculator.calculateResistors(listOf(10.0, 10.0))
        assertNotNull(rResult)
        assertEquals(20.0, rResult!!.seriesTotal, 0.01)
        assertEquals(5.0, rResult.parallelTotal, 0.01)

        val cResult = SeriesParallelCalculator.calculateCapacitors(listOf(10.0, 10.0))
        assertNotNull(cResult)
        assertEquals(5.0, cResult!!.seriesTotal, 0.01)
        assertEquals(20.0, cResult.parallelTotal, 0.01)
    }

    @Test
    fun testResistorColorCode() {
        val result = ResistorColorCode.decode4Band(
            ResistorColor.BROWN,
            ResistorColor.BLACK,
            ResistorColor.RED,
            ResistorColor.GOLD
        )
        assertEquals(1000.0, result.resistanceOhms, 0.01)
        assertEquals("1 kΩ", result.formattedResistance)
        assertEquals(5.0, result.tolerancePercent, 0.01)
    }

    @Test
    fun testSmdResistorCode() {
        val result3Digit = SmdResistorCode.decode("472")
        assertNotNull(result3Digit)
        assertEquals(4700.0, result3Digit!!.resistanceOhms, 0.01)

        val result4Digit = SmdResistorCode.decode("1002")
        assertNotNull(result4Digit)
        assertEquals(10000.0, result4Digit!!.resistanceOhms, 0.01)

        val resultEia96 = SmdResistorCode.decode("01A")
        assertNotNull(resultEia96)
        assertEquals(100.0, resultEia96!!.resistanceOhms, 0.01)
    }

    @Test
    fun testNumberSystemConverter() {
        val decResult = NumberSystemConverter.convertFromDecimal("255")
        assertTrue(decResult.isValid)
        assertEquals("11111111", decResult.binary)
        assertEquals("FF", decResult.hex)
        assertEquals("377", decResult.octal)
    }

    @Test
    fun testComponentsJsonDatasetIntegrity() {
        val fileCandidates = listOf(
            File("src/main/assets/components.json"),
            File("app/src/main/assets/components.json"),
            File("c:/MyProject/Mycreate/Electronics/app/src/main/assets/components.json")
        )
        val jsonFile = fileCandidates.firstOrNull { it.exists() }
        assertNotNull("components.json file must exist", jsonFile)
        val content = jsonFile!!.readText(Charsets.UTF_8)
        assertTrue("components.json must contain partNumber", content.contains("partNumber"))
        assertTrue("components.json must contain componentName", content.contains("componentName"))
    }
}
