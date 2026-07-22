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
    const val INSTAGRAM_URL = "https://instagram.com/di7xu"
    const val YOUTUBE_URL = "https://youtube.com/@m.dilip07"

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

    // Dynamic one-tap download link sharing matching the current app version
    fun shareApp(context: Context) {
        try {
            val versionName = DeviceInfoHelper.getAppVersion(context)
            val directDownloadUrl = "https://github.com/dk2017044/ElectroKit/raw/main/Build_Releases_APK/ElectroKit_v$versionName.apk"
            val releasesUrl = "https://github.com/dk2017044/ElectroKit/releases"

            val shareMessage = """
                ⚡ Download ElectroKit v$versionName ⚡
                Modern Offline Electronics Toolkit (400+ Components & Calculators).
                
                📥 One-Click Direct Download Link (Check your browser downloads after tapping):
                $directDownloadUrl
                
                🔄 Version History & Alternative Downloads:
                $releasesUrl
                
                Created by Dilip Kumar
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "ElectroKit App Download")
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share ElectroKit Download Link via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
