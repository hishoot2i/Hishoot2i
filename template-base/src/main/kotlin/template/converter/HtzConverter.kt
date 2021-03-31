package template.converter

import template.Template

interface HtzConverter {
    fun convert(template: Template): String
}
