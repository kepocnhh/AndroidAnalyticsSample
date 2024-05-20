package test.android.analytics

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.Base64
import java.util.Date

internal class EventsActivity : AppCompatActivity() {
    private val eventsID = View.generateViewId()

    private fun ByteArray.base64(): String {
        return String(Base64.getDecoder().decode(this))
    }

    private suspend fun printEvents(viewGroup: ViewGroup, file: File) {
        TextView(this).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            it.text = file.name
            it.setTextColor(Color.BLACK)
            viewGroup.addView(it)
        }
        withContext(Dispatchers.Default) {
            file.readLines().map { line ->
                val event = JSONObject(line.toByteArray().base64())
                val builder = StringBuilder()
                    .append("id: ${event.getString("id")}")
                    .append("\n")
                    .append("title: \"${event.getString("title")}\"")
                    .append("\n")
                    .append("reported: ${Date(event.getLong("reported"))}")
                event.getJSONObject("customKeys").also { customs ->
                    builder.append("\n")
                        .append("customs:")
                    for (key in customs.keys()) {
                        builder
                            .append("\n")
                            .append("\t")
                            .append("\"$key\": \"${customs.getString(key)}\"")
                    }
                }
                event.getJSONObject("messages").also { messages ->
                    builder.append("\n")
                        .append("messages:")
                    for (key in messages.keys().asSequence().sorted()) {
                        builder
                            .append("\n")
                            .append("\t")
                            .append("${Date(key.toLong())}:")
                            .append("\n")
                            .append("\t")
                            .append("\t")
                            .append("\"${messages.getString(key)}\"")
                    }
                }
                builder.toString()
            }
        }.forEach { text ->
            View(this).also {
                it.layoutParams = ViewGroup.LayoutParams(
                    0,
                    32,
                )
                viewGroup.addView(it)
            }
            TextView(this).also {
                it.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                it.text = text
                it.setTextColor(Color.BLACK)
                viewGroup.addView(it)
            }
        }
    }

    private fun printEvents() {
        val viewGroup: ViewGroup = findViewById(eventsID)
        viewGroup.removeAllViews()
        lifecycle.coroutineScope.launch {
            val file = withContext(Dispatchers.Default) {
                val external = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                if (external == null) TODO()
                val dir = external.resolve(BuildConfig.APPLICATION_ID)
                dir.listFiles()?.sortedBy { it.name }?.lastOrNull()
            }
            if (file == null) {
                TextView(this@EventsActivity).also {
                    it.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    it.text = "no events"
                    it.setTextColor(Color.BLACK)
                    viewGroup.addView(it)
                }
            } else {
                printEvents(viewGroup, file)
            }
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
        ScrollView(this).also { sv ->
            sv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            LinearLayout(this).also { column ->
                column.id = eventsID
                column.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                column.orientation = LinearLayout.VERTICAL
                sv.addView(column)
            }
            root.addView(sv)
        }
        setContentView(root)
        printEvents()
    }
}
