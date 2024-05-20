package test.android.analytics.provider

import android.os.Environment
import org.json.JSONObject
import test.android.analytics.BuildConfig
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class FinalAnalytics : Analytics {
    private val map = mutableMapOf<String, String>()
    private val messages = mutableMapOf<Duration, String>()

    override fun log(message: String) {
        val now = System.currentTimeMillis().milliseconds
        messages[now] = message
    }

    override fun setCustomKey(key: String, value: String) {
        map[key] = value
    }

    override fun record(error: Throwable) {
        TODO("Not yet implemented: record")
    }

    private data class Event(
        val id: UUID,
        val title: String,
        val reported: Duration,
        val customKeys: Map<String, String>,
        val messages: Map<Duration, String>
    )

    private fun <T : Any> Map<T, String>.toJSONObject(keys: (T) -> String): JSONObject {
        val obj = JSONObject()
        forEach { (key, value) ->
            obj.put(keys(key), value)
        }
        return obj
    }

    private fun Event.toJSONObject(): JSONObject {
        return JSONObject()
            .put("id", id.toString())
            .put("title", title)
            .put("reported", reported.inWholeMilliseconds)
            .put("customKeys", JSONObject(customKeys))
            .put("messages", messages.toJSONObject { it.inWholeMilliseconds.toString() })
    }

    private fun String.base64(): ByteArray {
        return Base64.getEncoder().encode(toByteArray())
    }

    override fun report(title: String, entries: Map<String, String>) {
        val external = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        if (external == null) TODO("No external files dir!")
        if (!external.exists()) TODO("External files dir \"$external\" does not exist!")
        if (!external.isDirectory) TODO("File \"$external\" is not a directory!")
        val dir = external.resolve(BuildConfig.APPLICATION_ID)
        if (dir.exists()) {
            if (!dir.isDirectory) TODO("File \"$dir\" is not a directory!")
        } else {
            if (!dir.mkdir()) TODO("Make dir \"$dir\" error!")
            if (!dir.exists()) TODO("File \"$dir\" does not exist!")
            if (!dir.isDirectory) TODO("File \"$dir\" is not a directory!")
        }
        val now = System.currentTimeMillis().milliseconds
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val fileName = "log-v0.2-${dateFormat.format(Date(now.inWholeMilliseconds))}.txt"
        val file = dir.resolve(fileName)
        val event = Event(
            id = UUID.randomUUID(),
            title = title,
            reported = now,
            customKeys = map + entries,
            messages = messages.toMap(),
        )
        file.appendBytes(event.toJSONObject().toString().base64() + '\n'.code.toByte())
    }
}
