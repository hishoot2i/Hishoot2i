package template.factory

import template.Template

interface Factory<out T : Template> {
    @Throws(Exception::class) fun newTemplate(): T
}
