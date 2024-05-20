package test.android.analytics.provider

import android.content.Context
import test.android.analytics.BuildConfig
import java.util.UUID

internal class FinalLocals(
    context: Context,
) : Locals {
    private val prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override val id: UUID
        get() {
            val text = prefs.getString("id", null)
            if (text == null) {
                val id = UUID.randomUUID()
                prefs.edit()
                    .putString("id", id.toString())
                    .commit()
                return id
            }
            return UUID.fromString(text)
        }
}
