package org.illegaller.ratabb.hishoot2i.data.pref.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import pref.SimplePref
import pref.ext.enumOrdinalPref
import template.TemplateComparator
import template.TemplateComparator.NAME_ASC
import javax.inject.Inject

class TemplatePrefImpl @Inject constructor(
    @ApplicationContext context: Context
) : TemplatePref, SimplePref(context, "template_pref") {
    override var templateComparator: TemplateComparator by enumOrdinalPref(NAME_ASC)
}
