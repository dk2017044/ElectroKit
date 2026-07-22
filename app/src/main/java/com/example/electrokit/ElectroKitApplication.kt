package com.example.electrokit

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.electrokit.data.repository.ComponentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ElectroKitApplication : Application(), Application.ActivityLifecycleCallbacks {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var repository: ComponentRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(this)
        setupCrashHandler()

        logActivity("App onCreate - Initializing ElectroKitApplication")

        repository = ComponentRepository(this)
        applicationScope.launch {
            try {
                logActivity("Starting Room DB pre-population in background")
                repository.initDatabaseIfNeeded()
                logActivity("Room DB pre-population finished successfully")
            } catch (e: Exception) {
                logCrash("Exception during initDatabaseIfNeeded", e)
            }
        }
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logCrash("Uncaught exception on thread ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun logActivity(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val logLine = "[$timestamp] [ACTIVITY] $message\n"
        Log.i("ELECTROKIT_LOG", logLine)
        appendToFile(logLine)
    }

    fun logCrash(contextMessage: String, throwable: Throwable) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTraceStr = sw.toString()
        val logLine = "[$timestamp] [CRASH_ERROR] $contextMessage\n$stackTraceStr\n----------------------------------------\n"
        Log.e("ELECTROKIT_LOG", logLine)
        appendToFile(logLine)
    }

    private fun appendToFile(text: String) {
        try {
            val logFile = File(getExternalFilesDir(null) ?: filesDir, "electrokit_crash.log")
            logFile.appendText(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Activity Lifecycle Tracking
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logActivity("Activity Created: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        logActivity("Activity Started: ${activity.javaClass.simpleName}")
    }

    override fun onActivityResumed(activity: Activity) {
        logActivity("Activity Resumed: ${activity.javaClass.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        logActivity("Activity Paused: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        logActivity("Activity Stopped: ${activity.javaClass.simpleName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logActivity("Activity SaveInstanceState: ${activity.javaClass.simpleName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logActivity("Activity Destroyed: ${activity.javaClass.simpleName}")
    }

    companion object {
        lateinit var instance: ElectroKitApplication
            private set
    }
}
