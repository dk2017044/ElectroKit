package com.example.electrokit.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.electrokit.data.database.entity.ComponentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ElectroKitOpenHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    "electrokit_native.db",
    null,
    1
) {

    private val _componentsFlow = MutableStateFlow<List<ComponentEntity>>(emptyList())
    val componentsFlow: StateFlow<List<ComponentEntity>> = _componentsFlow.asStateFlow()

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS components (
                partNumber TEXT PRIMARY KEY,
                componentName TEXT,
                category TEXT,
                type TEXT,
                description TEXT,
                packageType TEXT,
                pinCount TEXT,
                pinConfiguration TEXT,
                inputVoltage TEXT,
                outputVoltage TEXT,
                maxVoltage TEXT,
                minVoltage TEXT,
                maxCurrent TEXT,
                powerRating TEXT,
                frequency TEXT,
                operatingTemperature TEXT,
                gain TEXT,
                resistance TEXT,
                capacitance TEXT,
                inductance TEXT,
                accuracy TEXT,
                speed TEXT,
                manufacturer TEXT,
                applicationsRaw TEXT,
                advantagesRaw TEXT,
                limitationsRaw TEXT,
                equivalentComponentsRaw TEXT,
                keywordsRaw TEXT,
                datasheetSummary TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS components")
        onCreate(db)
    }

    fun getCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM components", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun insertAll(components: List<ComponentEntity>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (comp in components) {
                val cv = ContentValues().apply {
                    put("partNumber", comp.partNumber)
                    put("componentName", comp.componentName)
                    put("category", comp.category)
                    put("type", comp.type)
                    put("description", comp.description)
                    put("packageType", comp.packageType)
                    put("pinCount", comp.pinCount)
                    put("pinConfiguration", comp.pinConfiguration)
                    put("inputVoltage", comp.inputVoltage)
                    put("outputVoltage", comp.outputVoltage)
                    put("maxVoltage", comp.maxVoltage)
                    put("minVoltage", comp.minVoltage)
                    put("maxCurrent", comp.maxCurrent)
                    put("powerRating", comp.powerRating)
                    put("frequency", comp.frequency)
                    put("operatingTemperature", comp.operatingTemperature)
                    put("gain", comp.gain)
                    put("resistance", comp.resistance)
                    put("capacitance", comp.capacitance)
                    put("inductance", comp.inductance)
                    put("accuracy", comp.accuracy)
                    put("speed", comp.speed)
                    put("manufacturer", comp.manufacturer)
                    put("applicationsRaw", comp.applicationsRaw)
                    put("advantagesRaw", comp.advantagesRaw)
                    put("limitationsRaw", comp.limitationsRaw)
                    put("equivalentComponentsRaw", comp.equivalentComponentsRaw)
                    put("keywordsRaw", comp.keywordsRaw)
                    put("datasheetSummary", comp.datasheetSummary)
                }
                db.insertWithOnConflict("components", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        refreshList()
    }

    fun refreshList() {
        _componentsFlow.value = getAllComponents()
    }

    fun getAllComponents(): List<ComponentEntity> {
        val list = mutableListOf<ComponentEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM components ORDER BY componentName ASC", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(parseCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun searchComponents(query: String): List<ComponentEntity> {
        if (query.isBlank()) return getAllComponents()
        val list = mutableListOf<ComponentEntity>()
        val db = readableDatabase
        val q = "%${query.trim()}%"
        val sql = """
            SELECT * FROM components WHERE 
            partNumber LIKE ? OR 
            componentName LIKE ? OR 
            category LIKE ? OR 
            keywordsRaw LIKE ? OR 
            manufacturer LIKE ? OR 
            equivalentComponentsRaw LIKE ? OR 
            description LIKE ?
            ORDER BY componentName ASC
        """.trimIndent()
        val cursor = db.rawQuery(sql, arrayOf(q, q, q, q, q, q, q))
        if (cursor.moveToFirst()) {
            do {
                list.add(parseCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getComponentByPartNumber(partNumber: String): ComponentEntity? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM components WHERE partNumber = ? LIMIT 1", arrayOf(partNumber))
        var comp: ComponentEntity? = null
        if (cursor.moveToFirst()) {
            comp = parseCursor(cursor)
        }
        cursor.close()
        return comp
    }

    private fun parseCursor(cursor: android.database.Cursor): ComponentEntity {
        return ComponentEntity(
            partNumber = cursor.getString(cursor.getColumnIndexOrThrow("partNumber")),
            componentName = cursor.getString(cursor.getColumnIndexOrThrow("componentName")),
            category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
            type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            packageType = cursor.getString(cursor.getColumnIndexOrThrow("packageType")),
            pinCount = cursor.getString(cursor.getColumnIndexOrThrow("pinCount")),
            pinConfiguration = cursor.getString(cursor.getColumnIndexOrThrow("pinConfiguration")),
            inputVoltage = cursor.getString(cursor.getColumnIndexOrThrow("inputVoltage")),
            outputVoltage = cursor.getString(cursor.getColumnIndexOrThrow("outputVoltage")),
            maxVoltage = cursor.getString(cursor.getColumnIndexOrThrow("maxVoltage")),
            minVoltage = cursor.getString(cursor.getColumnIndexOrThrow("minVoltage")),
            maxCurrent = cursor.getString(cursor.getColumnIndexOrThrow("maxCurrent")),
            powerRating = cursor.getString(cursor.getColumnIndexOrThrow("powerRating")),
            frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency")),
            operatingTemperature = cursor.getString(cursor.getColumnIndexOrThrow("operatingTemperature")),
            gain = cursor.getString(cursor.getColumnIndexOrThrow("gain")),
            resistance = cursor.getString(cursor.getColumnIndexOrThrow("resistance")),
            capacitance = cursor.getString(cursor.getColumnIndexOrThrow("capacitance")),
            inductance = cursor.getString(cursor.getColumnIndexOrThrow("inductance")),
            accuracy = cursor.getString(cursor.getColumnIndexOrThrow("accuracy")),
            speed = cursor.getString(cursor.getColumnIndexOrThrow("speed")),
            manufacturer = cursor.getString(cursor.getColumnIndexOrThrow("manufacturer")),
            applicationsRaw = cursor.getString(cursor.getColumnIndexOrThrow("applicationsRaw")),
            advantagesRaw = cursor.getString(cursor.getColumnIndexOrThrow("advantagesRaw")),
            limitationsRaw = cursor.getString(cursor.getColumnIndexOrThrow("limitationsRaw")),
            equivalentComponentsRaw = cursor.getString(cursor.getColumnIndexOrThrow("equivalentComponentsRaw")),
            keywordsRaw = cursor.getString(cursor.getColumnIndexOrThrow("keywordsRaw")),
            datasheetSummary = cursor.getString(cursor.getColumnIndexOrThrow("datasheetSummary"))
        )
    }
}
