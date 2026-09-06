package org.nktnet.middor

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nktnet.middor.config.Screen
import org.nktnet.middor.config.ThemeOption
import org.nktnet.middor.config.UserSettings
import org.nktnet.middor.managers.ScreenCaptureManager
import org.nktnet.middor.services.MirrorService
import org.nktnet.middor.services.QuickBubbleService
import org.nktnet.middor.ui.components.PermissionOnboardingDialog
import org.nktnet.middor.ui.screens.HelpScreen
import org.nktnet.middor.ui.screens.InfoScreen
import org.nktnet.middor.ui.screens.LandingScreen
import org.nktnet.middor.ui.screens.SettingsScreen
import org.nktnet.middor.ui.theme.MiddorTheme

class MainActivity : ComponentActivity() {
    private lateinit var screenCaptureManager: ScreenCaptureManager
    private var sysBarTop = 0
    private var sysBarBottom = 0
    private var sysBarLeft = 0
    private var sysBarRight = 0
    private var isRequestingCapture = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var settingsLoaded by mutableStateOf(false)
        UserSettings.init(this) { settingsLoaded = true }

        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            if (!isRequestingCapture) {
                val sysBars = insets.getInsets(WindowInsets.Type.systemBars())
                sysBarTop = sysBars.top
                sysBarBottom = sysBars.bottom
                sysBarLeft = sysBars.left
                sysBarRight = sysBars.right
            }
            insets
        }

        screenCaptureManager = ScreenCaptureManager(
            this,
            updateIsRequesting = { isRequestingCapture = it }
        ) { resultCode, data ->
            val serviceIntent = Intent(this, MirrorService::class.java)
                .apply {
                    action = MirrorService.ACTION_START_OVERLAY
                    putExtra(MirrorService.EXTRA_RESULT_CODE, resultCode)
                    putExtra(MirrorService.EXTRA_RESULT_INTENT, data)
                    putExtra(MirrorService.EXTRA_CROP_TOP, sysBarTop)
                    putExtra(MirrorService.EXTRA_CROP_BOTTOM, sysBarBottom)
                    putExtra(MirrorService.EXTRA_CROP_LEFT, sysBarLeft)
                    putExtra(MirrorService.EXTRA_CROP_RIGHT, sysBarRight)
                }
            startForegroundService(serviceIntent)
        }

        setContent {
            val themeOption by UserSettings.currentTheme
            val isDarkTheme = resolveTheme(themeOption)
            val navController = rememberNavController()

            var overlayGranted by remember {
                mutableStateOf(Settings.canDrawOverlays(this@MainActivity))
            }

            val insetsController = remember(window) {
                window?.let { WindowInsetsControllerCompat(it, it.decorView) }
            }

            LaunchedEffect(isDarkTheme) {
                insetsController?.isAppearanceLightStatusBars = !isDarkTheme
                insetsController?.isAppearanceLightNavigationBars = !isDarkTheme
            }

            DisposableEffect(Unit) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        overlayGranted = Settings.canDrawOverlays(this@MainActivity)
                    }
                }
                lifecycle.addObserver(observer)
                onDispose { lifecycle.removeObserver(observer) }
            }

            LaunchedEffect(Unit) {
                snapshotFlow { UserSettings.quickBubbleEnabled.value && overlayGranted }
                    .collect { shouldRun ->
                        val intent = Intent(
                            this@MainActivity,
                            QuickBubbleService::class.java
                        )
                        if (shouldRun) {
                            if (!QuickBubbleService.isRunning) {
                                ContextCompat.startForegroundService(
                                    this@MainActivity,
                                    intent
                                )
                            }
                        } else {
                            stopService(intent)
                        }
                    }
            }

            MiddorTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Landing.route
                    ) {
                        composable(Screen.Landing.route) {
                            LandingScreen(
                                navController = navController,
                                screenCaptureManager = screenCaptureManager,
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController)
                        }
                        composable(Screen.Info.route) {
                            InfoScreen(navController)
                        }
                        composable(Screen.Help.route) {
                            HelpScreen(navController)
                        }
                    }

                    if (settingsLoaded && !UserSettings.permissionOnboardingCompleted.value) {
                        PermissionOnboardingDialog(
                            onDismiss = {
                                UserSettings.setPermissionOnboardingCompleted(this@MainActivity)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        when (intent.action) {
            MirrorService.ACTION_STOP_SERVICE -> {
                stopService(Intent(this, MirrorService::class.java))
            }
            else -> Unit
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, MirrorService::class.java))
        super.onDestroy()
    }

    @Composable
    private fun resolveTheme(theme: ThemeOption): Boolean {
        return when (theme) {
            ThemeOption.SYSTEM -> isSystemInDarkTheme()
            ThemeOption.DARK -> true
            ThemeOption.LIGHT -> false
        }
    }
}
