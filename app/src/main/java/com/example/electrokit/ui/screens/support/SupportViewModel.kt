package com.example.electrokit.ui.screens.support

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SupportViewModel : ViewModel() {

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    fun checkForUpdates(context: Context) {
        SupportIntentHelper.checkForUpdates(context) { errorMessage ->
            showSnackbar(errorMessage)
        }
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
