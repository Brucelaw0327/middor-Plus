package org.nktnet.middor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import org.nktnet.middor.managers.ScreenCaptureManager
import org.nktnet.middor.services.MirrorService
import org.nktnet.middor.services.QuickBubbleService

class QuickMirrorActivity : ComponentActivity() {

    private var startedMirror = false
    private var requestedCapture = false

    private val screenCaptureManager = ScreenCaptureManager(
        this,
        updateIsRequesting = { requesting ->
            if (!requesting) finish()
        },
        onCaptureResult = { resultCode, data ->
            startedMirror = true
            startForegroundService(
                Intent(this, MirrorService::class.java).apply {
                    action = MirrorService.ACTION_START_OVERLAY
                    putExtra(MirrorService.EXTRA_RESULT_CODE, resultCode)
                    putExtra(MirrorService.EXTRA_RESULT_INTENT, data)
                }
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (!requestedCapture) {
            requestedCapture = true
            screenCaptureManager.requestCapture()
        }
    }

    override fun onDestroy() {
        if (!startedMirror) {
            // Consent was dismissed or failed: bring the bubble back
            QuickBubbleService.notifyMirrorStopped()
        }
        super.onDestroy()
    }
}
