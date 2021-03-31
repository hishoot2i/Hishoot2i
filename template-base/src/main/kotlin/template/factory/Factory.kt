package template.factory

import template.Template

interface Factory<out T : Template> {
    fun newTemplate(): T
}
