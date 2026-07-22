package com.example.electrokit.ui.screens.support

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.electrokit.domain.utils.UpdateInfo
import com.example.electrokit.domain.utils.UpdateManager

class SupportViewModel : ViewModel() {

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    var isCheckingForUpdates by mutableStateOf(false)
        private set

    var latestUpdateInfo by mutableStateOf<UpdateInfo?>(null)
        private set

    var updateError by mutableStateOf<String?>(null)
        private set

    var showUpToDateDialog by mutableStateOf(false)
    
    var upToDateVersion by mutableStateOf("")

    var showErrorDialog by mutableStateOf(false)
    
    var errorDialogMsg by mutableStateOf("")

    var isDownloading by mutableStateOf(false)
        private set

    var downloadProgress by mutableStateOf(0f)
        private set

    var isFetchingAllReleases by mutableStateOf(false)
        private set

    var allReleases by mutableStateOf<List<UpdateInfo>>(emptyList())
        private set

    private var activeDownloadId = -1L

    fun checkForUpdates(context: Context) {
        isCheckingForUpdates = true
        latestUpdateInfo = null
        updateError = null
        showUpToDateDialog = false
        showErrorDialog = false

        UpdateManager.checkForUpdates(context) { result ->
            isCheckingForUpdates = false
            result.fold(
                onSuccess = { info ->
                    if (info.isNewer) {
                        latestUpdateInfo = info
                    } else {
                        upToDateVersion = info.latestVersion
                        showUpToDateDialog = true
                    }
                },
                onFailure = { error ->
                    updateError = error.message
                    errorDialogMsg = if (error is java.net.UnknownHostException || error.message?.contains("Unable to resolve host") == true) {
                        "No internet connection. Please check your network settings and try again."
                    } else {
                        error.message ?: "Unable to contact update server. Please try again later."
                    }
                    showErrorDialog = true
                }
            )
        }
    }

    fun fetchAllReleases(context: Context) {
        isFetchingAllReleases = true
        UpdateManager.fetchAllReleases(context) { result ->
            isFetchingAllReleases = false
            result.fold(
                onSuccess = { list ->
                    allReleases = list
                },
                onFailure = { error ->
                    showSnackbar(error.message ?: "Failed to fetch version history.")
                }
            )
        }
    }

    fun startDownload(context: Context, downloadUrl: String, version: String, releaseNotes: String) {
        if (isDownloading) {
            showSnackbar("A download is already in progress.")
            return
        }
        isDownloading = true
        downloadProgress = 0f
        activeDownloadId = UpdateManager.startDownload(
            context = context,
            downloadUrl = downloadUrl,
            latestVersion = version,
            releaseNotes = releaseNotes,
            onProgress = { progress ->
                downloadProgress = progress
            },
            onState = { stateMsg ->
                showSnackbar(stateMsg)
                if (stateMsg.contains("Installing") || stateMsg.contains("failed") || stateMsg.contains("failed or was canceled") || stateMsg.contains("WARNING") || stateMsg.contains("verification failed")) {
                    isDownloading = false
                    downloadProgress = 0f
                }
            }
        )
    }

    fun startDownload(context: Context) {
        val info = latestUpdateInfo ?: return
        startDownload(context, info.downloadUrl, info.latestVersion, info.releaseNotes)
        latestUpdateInfo = null
    }

    fun clearUpdateState() {
        latestUpdateInfo = null
        updateError = null
    }

    fun clearAllReleases() {
        allReleases = emptyList()
    }

    fun openInstagram(context: Context) {
        SupportIntentHelper.openInstagram(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun openYouTube(context: Context) {
        SupportIntentHelper.openYouTube(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun openGoogleForm(context: Context) {
        SupportIntentHelper.openGoogleFormDirectly(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun sendFeedback(context: Context) {
        SupportIntentHelper.sendFeedbackEmail(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun reportBug(context: Context) {
        SupportIntentHelper.sendBugReportEmail(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun requestNewComponent(context: Context) {
        SupportIntentHelper.openGoogleFormDirectly(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun requestNewFeature(context: Context) {
        SupportIntentHelper.openGoogleFormDirectly(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
    }

    fun rateApp(context: Context) {
        SupportIntentHelper.rateApp(context) { message ->
            showSnackbar(message)
        }
    }

    fun shareApp(context: Context) {
        SupportIntentHelper.shareApp(context)
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }
}
