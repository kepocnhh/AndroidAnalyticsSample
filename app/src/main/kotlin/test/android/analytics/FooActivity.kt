package test.android.analytics

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

internal class FooActivity : AppCompatActivity() {
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
        Button(this).also {
            it.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            )
            it.text = "start"
            it.setOnClickListener {
                // todo
            }
            root.addView(it)
        }
        setContentView(root)
    }
}
