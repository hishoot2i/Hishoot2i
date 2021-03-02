package org.illegaller.ratabb.hishoot2i.ui.template

import template.Template
import template.Template.VersionHtz

internal sealed class TemplateView
internal class Fail(val cause: Throwable) : TemplateView()
internal class HtzImported(val htz: VersionHtz) : TemplateView()
internal object Loading : TemplateView()
internal class Success(val data: List<Template>) : TemplateView()
