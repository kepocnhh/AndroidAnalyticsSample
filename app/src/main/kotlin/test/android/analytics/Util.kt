package test.android.analytics

import android.graphics.Rect
import android.os.Build
import android.view.WindowInsets

internal fun WindowInsets.toRect(): Rect {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getInsets(WindowInsets.Type.systemBars()).let {
            Rect(
                it.left,
                it.top,
                it.right,
                it.bottom,
            )
        }
    } else {
        Rect(
            systemWindowInsetLeft,
            systemWindowInsetTop,
            systemWindowInsetRight,
            systemWindowInsetBottom,
        )
    }
}
