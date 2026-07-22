package com.example.electrokit.domain.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DeviceInfoHelper {

    fun getAppVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    fun getAndroidVersion(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }

    fun getDeviceModel(): String {
        return Build.MODEL.capitalizeWords()
    }

    fun getManufacturer(): String {
        return Build.MANUFACTURER.capitalizeWords()
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        return sdf.format(Date())
    }

    fun generateFeedbackEmailBody(context: Context): String {
        return """
----------------------------------------------------
ElectroKit Feedback

App Version: ${getAppVersion(context)}
Android Version: ${getAndroidVersion()}
Device Model: ${getDeviceModel()}
Manufacturer: ${getManufacturer()}
Date & Time: ${getCurrentDateTime()}

Feedback:

----------------------------------------------------
        """.trimIndent()
    }

    fun generateBugReportEmailBody(context: Context): String {
        return """
----------------------------------------------------
Bug Report

App Version: ${getAppVersion(context)}
Android Version: ${getAndroidVersion()}
Device Model: ${getDeviceModel()}
Manufacturer: ${getManufacturer()}
Date & Time: ${getCurrentDateTime()}

Screen Name: 

Issue Description: 

Steps To Reproduce: 
1. 
2. 

Expected Result: 

Actual Result: 

Additional Notes: 

----------------------------------------------------
        """.trimIndent()
    }

    private fun String.capitalizeWords(): String {
        return this.split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
    }
}
