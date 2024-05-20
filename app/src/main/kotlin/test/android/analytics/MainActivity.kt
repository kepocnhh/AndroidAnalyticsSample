package test.android.analytics

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

internal class MainActivity : AppCompatActivity() {
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
        LinearLayout(this).also { column ->
            column.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL,
            )
            column.orientation = LinearLayout.VERTICAL
            Button(this).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                it.text = "foo"
                it.setOnClickListener {
                    App.injection.analytics.log("go to foo")
                    val intent = Intent(this, FooActivity::class.java)
                    startActivity(intent)
                }
                column.addView(it)
            }
            Button(this).also {
                it.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                it.text = "events"
                it.setOnClickListener {
                    App.injection.analytics.log("go to events")
                    val intent = Intent(this, EventsActivity::class.java)
                    startActivity(intent)
                }
                column.addView(it)
            }
            root.addView(column)
        }
        setContentView(root)
    }
}
