package org.illegaller.ratabb.hishoot2i.ui.tools.badge

internal sealed class BadgeView
internal class Fail(val cause: Throwable) : BadgeView()
internal class Success(val data: List<String>) : BadgeView()
