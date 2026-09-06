package org.nktnet.middor.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.ImageButton
import androidx.core.graphics.toColorInt
import java.lang.ref.WeakReference
import org.nktnet.middor.QuickMirrorActivity
import org.nktnet.middor.R
import org.nktnet.middor.managers.CustomNotificationManager

class QuickBubbleService : Service() {

    private var bubbleView: ImageButton? = null
    private var bubbleParams: WindowManager.LayoutParams? = null
    private var bubbleAdded = false

    companion object {
        private val BUBBLE_COLOUR = "#80404040".toColorInt()
        private const val BUBBLE_SIZE_DP = 56

        @Volatile
        private var instanceRef: WeakReference<QuickBubbleService>? = null

        val isRunning: Boolean
            get() = instanceRef?.get() != null

        fun notifyMirrorStarted() {
            instanceRef?.get()?.setBubbleVisible(false)
        }

        fun notifyMirrorStopped() {
            instanceRef?.get()?.setBubbleVisible(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instanceRef = WeakReference(this)
        CustomNotificationManager.createBubbleNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            2,
            CustomNotificationManager.buildBubbleNotification(this),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )
        addBubbleView()
        return START_STICKY
    }

    override fun onDestroy() {
        removeBubbleView()
        instanceRef?.clear()
        instanceRef = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun bubbleSizePx(): Int =
        (BUBBLE_SIZE_DP * resources.displayMetrics.density).toInt()

    private fun addBubbleView() {
        if (bubbleView == null) {
            bubbleView = createBubbleView()
            bubbleParams = createBubbleParams()
        }
        setBubbleVisible(true)
    }

    private fun removeBubbleView() {
        val view = bubbleView ?: return
        if (bubbleAdded) {
            runCatching {
                (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(view)
            }
            bubbleAdded = false
        }
        bubbleView = null
        bubbleParams = null
    }

    private fun setBubbleVisible(visible: Boolean) {
        val view = bubbleView ?: return
        val params = bubbleParams ?: return
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager

        if (visible && !bubbleAdded) {
            runCatching {
                wm.addView(view, params)
                bubbleAdded = true
            }.onFailure {
                // Overlay permission may have been revoked while running
                stopSelf()
            }
        } else if (!visible && bubbleAdded) {
            runCatching {
                wm.removeView(view)
                bubbleAdded = false
            }
        }
    }

    private fun createBubbleParams(): WindowManager.LayoutParams {
        val size = bubbleSizePx()
        val metrics = resources.displayMetrics
        return WindowManager.LayoutParams(
            size,
            size,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = 0
            y = (metrics.heightPixels - size) / 3
        }
    }

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    private fun createBubbleView(): ImageButton {
        val size = bubbleSizePx()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

        return ImageButton(this).apply {
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(BUBBLE_COLOUR)
            }
            background = shape
            setImageResource(R.drawable.flip_24px)
            setColorFilter(Color.WHITE)

            var downRawX = 0f
            var downRawY = 0f
            var startParamX = 0
            var startParamY = 0
            var isDragging = false

            setOnTouchListener { _, event ->
                val params = bubbleParams ?: return@setOnTouchListener false
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        downRawX = event.rawX
                        downRawY = event.rawY
                        startParamX = params.x
                        startParamY = params.y
                        isDragging = false
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - downRawX
                        val dy = event.rawY - downRawY
                        if (isDragging || dx * dx + dy * dy > touchSlop * touchSlop) {
                            isDragging = true
                            params.x = (startParamX + dx.toInt())
                                .coerceIn(0, metrics.widthPixels - size)
                            params.y = (startParamY + dy.toInt())
                                .coerceIn(0, metrics.heightPixels - size)
                            runCatching { wm.updateViewLayout(this, params) }
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            setBubbleVisible(false)
                            startActivity(
                                Intent(this@QuickBubbleService, QuickMirrorActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }
}
