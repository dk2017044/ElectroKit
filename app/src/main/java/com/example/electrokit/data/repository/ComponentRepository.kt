package com.example.electrokit.data.repository

import android.content.Context
import com.example.electrokit.data.database.ElectroKitOpenHelper
import com.example.electrokit.data.database.entity.ComponentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

class ComponentRepository(context: Context) {

    private val applicationContext: Context = context.applicationContext
    private val dbHelper = ElectroKitOpenHelper(applicationContext)

    suspend fun initDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            try {
                val count = dbHelper.getCount()
                val jsonString = applicationContext.assets.open("components.json").bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)

                if (count == 0 || count < jsonArray.length()) {
                    val entityList = mutableListOf<ComponentEntity>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val entity = ComponentEntity(
                            partNumber = obj.optString("partNumber"),
                            componentName = obj.optString("componentName"),
                            category = obj.optString("category"),
                            type = obj.optString("type"),
                            description = obj.optString("description"),
                            packageType = obj.optString("package"),
                            pinCount = obj.optString("pinCount"),
                            pinConfiguration = obj.optString("pinConfiguration"),
                            inputVoltage = obj.optString("inputVoltage"),
                            outputVoltage = obj.optString("outputVoltage"),
                            maxVoltage = obj.optString("maxVoltage"),
                            minVoltage = obj.optString("minVoltage"),
                            maxCurrent = obj.optString("maxCurrent"),
                            powerRating = obj.optString("powerRating"),
                            frequency = obj.optString("frequency"),
                            operatingTemperature = obj.optString("operatingTemperature"),
                            gain = obj.optString("gain"),
                            resistance = obj.optString("resistance"),
                            capacitance = obj.optString("capacitance"),
                            inductance = obj.optString("inductance"),
                            accuracy = obj.optString("accuracy"),
                            speed = obj.optString("speed"),
                            manufacturer = obj.optString("manufacturer"),
                            applicationsRaw = parseJsonArrayToString(obj.optJSONArray("applications")),
                            advantagesRaw = parseJsonArrayToString(obj.optJSONArray("advantages")),
                            limitationsRaw = parseJsonArrayToString(obj.optJSONArray("limitations")),
                            equivalentComponentsRaw = parseJsonArrayToString(obj.optJSONArray("equivalentComponents")),
                            keywordsRaw = parseJsonArrayToString(obj.optJSONArray("keywords")),
                            datasheetSummary = obj.optString("datasheetSummary")
                        )
                        entityList.add(entity)
                    }

                    dbHelper.insertAll(entityList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllComponents(): Flow<List<ComponentEntity>> = flow {
        emit(dbHelper.getAllComponents())
    }

    fun searchComponents(query: String): Flow<List<ComponentEntity>> = flow {
        emit(dbHelper.searchComponents(query))
    }

    fun getComponentsByPartNumbers(partNumbers: List<String>): Flow<List<ComponentEntity>> = flow {
        val all = dbHelper.getAllComponents()
        val filtered = all.filter { it.partNumber in partNumbers }
        emit(filtered)
    }

    suspend fun getComponentByPartNumber(partNumber: String): ComponentEntity? {
        return withContext(Dispatchers.IO) {
            dbHelper.getComponentByPartNumber(partNumber)
        }
    }

    private fun parseJsonArrayToString(jsonArray: JSONArray?): String {
        if (jsonArray == null || jsonArray.length() == 0) return ""
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.optString(i)
            if (item.isNotBlank()) list.add(item)
        }
        return list.joinToString(", ")
    }
}
