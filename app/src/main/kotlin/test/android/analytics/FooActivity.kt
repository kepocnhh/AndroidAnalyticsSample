package test.android.analytics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class FooActivity : AppCompatActivity() {
    private val timeTextViewID = View.generateViewId()
    private val startViewID = View.generateViewId()
    private val stopViewID = View.generateViewId()
    private var started: Duration? = null

    private fun stopTimer() {
        findViewById<View>(startViewID).also {
            it.isEnabled = true
            it.visibility = View.VISIBLE
        }
        findViewById<View>(stopViewID).also {
            it.isEnabled = false
            it.visibility = View.GONE
        }
        findViewById<TextView>(timeTextViewID).text = ""
        started = null
    }

    private fun startTimer() {
        findViewById<View>(startViewID).also {
            it.isEnabled = false
            it.visibility = View.GONE
        }
        findViewById<View>(stopViewID).also {
            it.isEnabled = true
            it.visibility = View.VISIBLE
        }
        lifecycle.coroutineScope.launch {
            withContext(Dispatchers.Default) {
                started = System.currentTimeMillis().milliseconds
                val time = 10.seconds
                val timeTextView = findViewById<TextView>(timeTextViewID)
                while (true) {
                    when (val started = started) {
                        null -> break
                        else -> {
                            val now = System.currentTimeMillis().milliseconds
                            val div = now - started
                            if (div < time) {
                                withContext(Dispatchers.Main) {
                                    val left = time - div
                                    val text = String.format("%02d:%03d left", left.inWholeSeconds, left.inWholeMilliseconds - left.inWholeSeconds * 1_000)
                                    timeTextView.text = text
                                }
                                delay(100)
                            } else {
                                App.injection.analytics.log("time is up")
                                // todo event
                                break
                            }
                        }
                    }
                }
            }
            stopTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            it.background = ColorDrawable(Color.WHITE)
            it.post {
                val insets = window!!.decorView.rootWindowInsets.toRect()
                it.setPadding(
                    insets.left,
                    insets.top,
                    insets.right,
                    insets.bottom,
                )
            }
        }
        TextView(this).also {
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            )
            it.text = this::class.java.name
            it.setTextColor(Color.BLACK)
            root.addView(it)
        }
        TextView(this).also {
            it.id = timeTextViewID
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER,
            )
            it.setTextColor(Color.BLACK)
            root.addView(it)
        }
        Button(this).also {
            it.id = startViewID
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            )
            it.text = "start"
            it.setOnClickListener {
                App.injection.analytics.log("click start timer")
                startTimer()
            }
            root.addView(it)
        }
        Button(this).also {
            it.id = stopViewID
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            )
            it.text = "stop"
            it.setOnClickListener {
                App.injection.analytics.log("click stop timer")
                val now = System.currentTimeMillis().milliseconds
                App.injection.analytics.report(
                    entries = mapOf(
                        "time:passed:ms" to (now - started!!).inWholeMilliseconds.toString(),
                    ),
                )
                started = null
            }
            it.isEnabled = false
            it.visibility = View.GONE
            root.addView(it)
        }
        setContentView(root)
    }
}
