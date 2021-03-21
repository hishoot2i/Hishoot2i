package org.illegaller.ratabb.hishoot2i.ui.template

import template.Template

internal sealed class TemplateView
internal class Fail(val cause: Throwable) : TemplateView()
internal object Loading : TemplateView()
internal class Success(val data: List<Template>) : TemplateView()

internal sealed class HtzEventView
internal class FailHtzEvent(val cause: Throwable) : HtzEventView()
internal class SuccessHtzEvent(val event: HtzEvent, val message: String) : HtzEventView()
internal object LoadingHtzEvent : HtzEventView()
internal enum class HtzEvent { IMPORT, CONVERT, EXPORT, REMOVE }
