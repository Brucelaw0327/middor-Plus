package org.nktnet.middor.ui.components

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.nktnet.middor.R

@Composable
fun PermissionOnboardingDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return
    val notificationManager = remember {
        context.getSystemService(NotificationManager::class.java)
    }

    var overlayGranted by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }
    var notificationsGranted by remember {
        mutableStateOf(notificationManager.areNotificationsEnabled())
    }
    var awaitingOverlayReturn by remember { mutableStateOf(false) }

    val dismissOnReturn by rememberUpdatedState(onDismiss)
    val isAwaitingOverlayReturn by rememberUpdatedState(awaitingOverlayReturn)

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationsGranted = granted
        if (!Settings.canDrawOverlays(context)) {
            awaitingOverlayReturn = true
            openOverlaySettings(context)
        } else {
            dismissOnReturn()
        }
    }

    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                val overlay = Settings.canDrawOverlays(context)
                overlayGranted = overlay
                notificationsGranted = notificationManager.areNotificationsEnabled()
                if (isAwaitingOverlayReturn && overlay) {
                    awaitingOverlayReturn = false
                    dismissOnReturn()
                }
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.permission_onboarding_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.permission_onboarding_message))
                OnboardingPermissionRow(
                    label = stringResource(R.string.permission_onboarding_notifications),
                    granted = notificationsGranted
                )
                OnboardingPermissionRow(
                    label = stringResource(R.string.permission_onboarding_overlay),
                    granted = overlayGranted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    overlayGranted && notificationsGranted -> dismissOnReturn()
                    !notificationsGranted ->
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else -> {
                        awaitingOverlayReturn = true
                        openOverlaySettings(context)
                    }
                }
            }) {
                Text(stringResource(R.string.permission_onboarding_grant))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.permission_onboarding_skip))
            }
        }
    )
}

@Composable
private fun OnboardingPermissionRow(label: String, granted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            stringResource(
                if (granted) {
                    R.string.permission_onboarding_granted
                } else {
                    R.string.permission_onboarding_not_granted
                }
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = if (granted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun openOverlaySettings(context: Context) {
    runCatching {
        context.startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
        )
    }.onFailure {
        Log.e("PermissionOnboarding", "Failed to open overlay settings", it)
    }
}
