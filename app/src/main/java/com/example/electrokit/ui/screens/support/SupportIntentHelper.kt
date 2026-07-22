package com.example.electrokit.ui.screens.support

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.electrokit.domain.utils.DeviceInfoHelper
import java.io.File

object SupportIntentHelper {

    private const val SUPPORT_EMAIL = "dk2017044@hotmail.com"
    const val GOOGLE_FORM_URL = "https://forms.gle/CXRwfpFUvrZUpjTj8"
    const val UPDATE_DRIVE_URL = "https://drive.google.com/drive/folders/1XP_pAGkSNf9SPz0c9-PcJGX5aX1W9_cM?usp=sharing"
    const val INSTAGRAM_URL = "https://instagram.com/di7xu"
    const val YOUTUBE_URL = "https://youtube.com/@m.dilip07"

    fun checkForUpdates(context: Context, onError: (String) -> Unit = {}) {
        openBrowserUrl(context, UPDATE_DRIVE_URL, onError)
    }

    fun openInstagram(context: Context, onError: (String) -> Unit = {}) {
        openBrowserUrl(context, INSTAGRAM_URL, onError)
    }

    fun openYouTube(context: Context, onError: (String) -> Unit = {}) {
        openBrowserUrl(context, YOUTUBE_URL, onError)
    }

    fun openGoogleFormDirectly(context: Context, onError: (String) -> Unit) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_FORM_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            onError("Unable to open Google Form in browser.")
        }
    }

    fun sendFeedbackEmail(context: Context, onError: (String) -> Unit) {
        val body = DeviceInfoHelper.generateFeedbackEmailBody(context)
        val subject = "ElectroKit Feedback & Suggestions"
        sendDirectEmail(context, subject, body, onError)
    }

    fun sendBugReportEmail(context: Context, onError: (String) -> Unit) {
        val body = DeviceInfoHelper.generateBugReportEmailBody(context)
        val subject = "ElectroKit Bug Report"
        sendDirectEmail(context, subject, body, onError)
    }

    private fun sendDirectEmail(
        context: Context,
        subject: String,
        body: String,
        onError: (String) -> Unit
    ) {
        try {
            val mailtoUri = Uri.parse("mailto:$SUPPORT_EMAIL")
                .buildUpon()
                .appendQueryParameter("subject", subject)
                .appendQueryParameter("body", body)
                .build()

            val emailIntent = Intent(Intent.ACTION_SENDTO, mailtoUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            onError("No Email client found. Developer Email: $SUPPORT_EMAIL")
        }
    }

    fun openBrowserUrl(context: Context, url: String, onError: (String) -> Unit) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            onError("Unable to open link in browser.")
        }
    }

    // Direct APK File Sharing — filename always matches current app versionName
    fun shareApp(context: Context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "2.0.2"

            val originalApk = File(context.applicationInfo.sourceDir)
            val shareDir = File(context.cacheDir, "shared_apk")
            shareDir.mkdirs()
            // Delete old cached APKs with stale version names
            shareDir.listFiles()?.forEach { it.delete() }
            val targetApk = File(shareDir, "ElectroKit_v${versionName}.apk")

            if (!targetApk.exists() || targetApk.length() != originalApk.length()) {
                originalApk.copyTo(targetApk, overwrite = true)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetApk
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "ElectroKit App APK (v$versionName)")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out ElectroKit v$versionName - Modern Offline Electronics Toolkit (400 Component Database & Calculators) by Dilip Kumar!"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share ElectroKit_v${versionName}.apk via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            shareAppText(context)
        }
    }

    private fun shareAppText(context: Context) {
        val shareMessage = """
Check out ElectroKit - Electronics Toolkit v2.0.1.

Features:
- Electronics Engineering Calculators
- 400+ Component Database
- Number System Converter
- Developed by Dilip Kumar
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "ElectroKit - Electronics Toolkit")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(shareIntent, "Share ElectroKit via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun rateApp(context: Context, onShowMessage: (String) -> Unit) {
        val packageName = context.packageName
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {
            onShowMessage("ElectroKit will be available on Google Play soon. Developer Email: $SUPPORT_EMAIL")
        }
    }
}
