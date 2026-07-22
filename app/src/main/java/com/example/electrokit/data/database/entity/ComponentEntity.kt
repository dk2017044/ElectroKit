package com.example.electrokit.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "components")
data class ComponentEntity(
    @PrimaryKey val partNumber: String,
    val componentName: String = "",
    val category: String = "",
    val type: String = "",
    val description: String = "",
    val packageType: String = "", // JSON key 'package'
    val pinCount: String = "",
    val pinConfiguration: String = "",
    val inputVoltage: String = "",
    val outputVoltage: String = "",
    val maxVoltage: String = "",
    val minVoltage: String = "",
    val maxCurrent: String = "",
    val powerRating: String = "",
    val frequency: String = "",
    val operatingTemperature: String = "",
    val gain: String = "",
    val resistance: String = "",
    val capacitance: String = "",
    val inductance: String = "",
    val accuracy: String = "",
    val speed: String = "",
    val manufacturer: String = "",
    val applicationsRaw: String = "", // Json/Comma separated
    val advantagesRaw: String = "",
    val limitationsRaw: String = "",
    val equivalentComponentsRaw: String = "",
    val keywordsRaw: String = "",
    val datasheetSummary: String = "",
    val isFavorite: Boolean = false
)
