package com.example.electrokit.domain.utils

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.regex.Pattern

data class UpdateInfo(
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val isNewer: Boolean
)

object UpdateManager {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    private const val GITHUB_RELEASE_API = "https://api.github.com/repos/dk2017044/ElectroKit/releases/latest"
    private const val GITHUB_ALL_RELEASES_API = "https://api.github.com/repos/dk2017044/ElectroKit/releases"
    private const val CHANNEL_ID = "electrokit_updates_channel"

    private var pendingInstallUri: Uri? = null

    fun checkForUpdates(context: Context, onResult: (Result<UpdateInfo>) -> Unit) {
        executor.execute {
            try {
                val url = URL(GITHUB_RELEASE_API)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.setRequestProperty("User-Agent", "ElectroKit-App")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val json = JSONObject(response.toString())
                    val tagName = json.getString("tag_name")
                    val body = json.optString("body", "No release notes available.")
                    val assets = json.getJSONArray("assets")
                    var downloadUrl = ""

                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.getString("name")
                        if (name.endsWith(".apk")) {
                            downloadUrl = asset.getString("browser_download_url")
                            break
                        }
                    }

                    if (downloadUrl.isEmpty()) {
                        mainHandler.post {
                            onResult(Result.failure(Exception("No APK file found in the latest release.")))
                        }
                        return@execute
                    }

                    val currentVersion = DeviceInfoHelper.getAppVersion(context)
                    val isNewer = isNewerVersion(currentVersion, tagName)

                    val updateInfo = UpdateInfo(
                        latestVersion = tagName,
                        downloadUrl = downloadUrl,
                        releaseNotes = body,
                        isNewer = isNewer
                    )

                    mainHandler.post {
                        onResult(Result.success(updateInfo))
                    }
                } else {
                    mainHandler.post {
                        onResult(Result.failure(Exception("Failed to connect to update server. Code: $responseCode")))
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    onResult(Result.failure(e))
                }
            }
        }
    }

    fun fetchAllReleases(context: Context, onResult: (Result<List<UpdateInfo>>) -> Unit) {
        executor.execute {
            try {
                val url = URL(GITHUB_ALL_RELEASES_API)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.setRequestProperty("User-Agent", "ElectroKit-App")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonArray = JSONArray(response.toString())
                    val releases = mutableListOf<UpdateInfo>()
                    val currentVersion = DeviceInfoHelper.getAppVersion(context)

                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val tagName = json.getString("tag_name")
                        val body = json.optString("body", "No release notes available.")
                        val assets = json.getJSONArray("assets")
                        var downloadUrl = ""

                        for (j in 0 until assets.length()) {
                            val asset = assets.getJSONObject(j)
                            val name = asset.getString("name")
                            if (name.endsWith(".apk")) {
                                downloadUrl = asset.getString("browser_download_url")
                                break
                            }
                        }

                        if (downloadUrl.isNotEmpty()) {
                            val isNewer = isNewerVersion(currentVersion, tagName)
                            releases.add(
                                UpdateInfo(
                                    latestVersion = tagName,
                                    downloadUrl = downloadUrl,
                                    releaseNotes = body,
                                    isNewer = isNewer
                                )
                            )
                        }
                    }

                    mainHandler.post {
                        onResult(Result.success(releases))
                    }
                } else {
                    mainHandler.post {
                        onResult(Result.failure(Exception("Failed to fetch releases. Code: $responseCode")))
                    }
                }
            } catch (e: Exception) {
                mainHandler.post {
                    onResult(Result.failure(e))
                }
            }
        }
    }

    fun isNewerVersion(current: String, latest: String): Boolean {
        val curClean = current.trim().removePrefix("v").removePrefix("V")
        val latClean = latest.trim().removePrefix("v").removePrefix("V")
        if (curClean == latClean) return false

        val curParts = curClean.split(".")
        val latParts = latClean.split(".")

        val maxLength = maxOf(curParts.size, latParts.size)
        for (i in 0 until maxLength) {
            val curVal = curParts.getOrNull(i)?.toIntOrNull() ?: 0
            val latVal = latParts.getOrNull(i)?.toIntOrNull() ?: 0
            if (latVal > curVal) return true
            if (curVal > latVal) return false
        }
        return false
    }

    fun startDownload(
        context: Context,
        downloadUrl: String,
        latestVersion: String,
        releaseNotes: String,
        onProgress: (Float) -> Unit,
        onState: (String) -> Unit
    ): Long {
        try {
            // Clean up any old leftover APKs from private storage first
            cleanUpLeftoverApks(context)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                setTitle("ElectroKit Update")
                setDescription("Downloading ElectroKit $latestVersion")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setMimeType("application/vnd.android.package-archive")
                // Store in app-private external files directory (NOT public Downloads folder)
                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "ElectroKit_Update.apk")
            }

            val downloadId = downloadManager.enqueue(request)
            onState("Downloading ElectroKit $latestVersion in background... Check notification bar for progress.")

            // Periodically check progress using an executor runnable loop
            var isRunning = true
            executor.execute {
                while (isRunning) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val bytesSoFarIdx = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        val bytesTotalIdx = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        if (bytesSoFarIdx != -1 && bytesTotalIdx != -1) {
                            val bytesDownloaded = cursor.getInt(bytesSoFarIdx)
                            val bytesTotal = cursor.getInt(bytesTotalIdx)
                            if (bytesTotal > 0) {
                                val progress = bytesDownloaded.toFloat() / bytesTotal.toFloat()
                                mainHandler.post { onProgress(progress) }
                            }
                        }
                        
                        val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (statusIdx != -1) {
                            val status = cursor.getInt(statusIdx)
                            if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                                isRunning = false
                            }
                        }
                    }
                    cursor?.close()
                    try {
                        Thread.sleep(800)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }

            // Receiver for install trigger + SHA256 check
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        isRunning = false
                        val query = DownloadManager.Query().setFilterById(downloadId)
                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst()) {
                            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            if (columnIndex != -1) {
                                val status = cursor.getInt(columnIndex)
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    val installUri = downloadManager.getUriForDownloadedFile(downloadId)
                                    val localPathIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                    val localUriString = if (localPathIdx != -1) cursor.getString(localPathIdx) else null
                                    
                                    if (installUri != null && localUriString != null) {
                                        val apkFile = File(Uri.parse(localUriString).path ?: "")
                                        
                                        // Run security checksum verification in background
                                        executor.execute {
                                            val expectedHash = extractSHA256(releaseNotes)
                                            if (expectedHash != null && apkFile.exists()) {
                                                val calculatedHash = calculateSHA256(apkFile)
                                                if (calculatedHash.equals(expectedHash, ignoreCase = true)) {
                                                    mainHandler.post {
                                                        onState("Security Check Passed! Redirecting to System Installer...")
                                                        installApk(c, installUri)
                                                    }
                                                } else {
                                                    mainHandler.post {
                                                        onState("SECURITY WARNING: SHA-256 Checksum verification failed! File might be tampered or corrupted.")
                                                    }
                                                }
                                            } else {
                                                mainHandler.post {
                                                    onState("Download complete. Redirecting to System Installer...")
                                                    installApk(c, installUri)
                                                }
                                            }
                                        }
                                    } else {
                                        mainHandler.post {
                                            onState("Download failed: Couldn't resolve local path.")
                                        }
                                    }
                                } else {
                                    mainHandler.post {
                                        onState("Download failed or was canceled.")
                                    }
                                }
                            }
                        }
                        cursor.close()
                        try {
                            c.unregisterReceiver(this)
                        } catch (e: Exception) {
                            // Ignored
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            }
            return downloadId
        } catch (e: Exception) {
            onState("Error starting download: ${e.message}")
            return -1L
        }
    }

    fun installApk(context: Context, fileUri: Uri) {
        try {
            // Check Unknown App Install permission on Android 8.0+ (Oreo to Android 15)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    pendingInstallUri = fileUri
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    return
                }
            }

            pendingInstallUri = null

            val contentUri = if (fileUri.scheme == "file") {
                val file = File(fileUri.path ?: "")
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                fileUri
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onResumeCheckPendingInstall(context: Context) {
        val uri = pendingInstallUri
        if (uri != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()) {
                installApk(context, uri)
            }
        }
    }

    fun cleanUpLeftoverApks(context: Context) {
        executor.execute {
            try {
                val dirsToClean = listOfNotNull(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    context.externalCacheDir,
                    context.cacheDir
                )

                dirsToClean.forEach { dir ->
                    if (dir.exists() && dir.isDirectory) {
                        dir.listFiles()?.forEach { file ->
                            if (file.isFile && file.name.endsWith(".apk")) {
                                file.delete()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showUpdateNotification(context: Context, latestVersion: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when a new version of ElectroKit is available."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open settings / check for updates screen on click
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            putExtra("navigate_to", "settings")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("New ElectroKit Update Available! 🚀")
            .setContentText("Version $latestVersion is now ready. Tap to view changes.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1001, builder.build())
    }

    private fun extractSHA256(text: String): String? {
        val pattern = Pattern.compile("\\b([a-fA-F0-9]{64})\\b")
        val matcher = pattern.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }

    private fun calculateSHA256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val inputStream = file.inputStream()
        val buffer = ByteArray(8192)
        var bytesRead: Int
        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        } finally {
            inputStream.close()
        }
        val shaBytes = digest.digest()
        return shaBytes.joinToString("") { "%02x".format(it) }
    }
}
