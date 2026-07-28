package com.example.electrokit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.electrokit.domain.utils.UpdateManager
import com.example.electrokit.ui.ElectroKitApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        UpdateManager.cleanUpLeftoverApks(this)
        setContent {
            ElectroKitApp()
        }
    }

    override fun onResume() {
        super.onResume()
        UpdateManager.onResumeCheckPendingInstall(this)
        UpdateManager.cleanUpLeftoverApks(this)
    }
}
