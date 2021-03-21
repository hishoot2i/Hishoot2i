package template.converter

import template.Template
import template.model.ModelHtz

internal interface HtzConverter {
    fun convert(template: Template, generatorHtzId: ModelHtz.() -> String): String
}
