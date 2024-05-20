package test.android.analytics.module.app

import test.android.analytics.provider.Analytics
import test.android.analytics.provider.Locals

internal class Injection(
    val locals: Locals,
    val analytics: Analytics,
)
