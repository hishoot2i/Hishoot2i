package org.illegaller.ratabb.hishoot2i.ui.tools.template

import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import template.Template

internal sealed class TemplateToolView
internal class Fail(val cause: Throwable) : TemplateToolView()
internal class Success(val template: Template, val pref: TemplateToolPref) : TemplateToolView()
