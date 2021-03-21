package template.factory

import template.Template

internal interface Factory<out T : Template> {
    fun newTemplate(): T
}
