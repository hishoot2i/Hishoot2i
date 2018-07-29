package rbb.hishoot2i.template.factory

import rbb.hishoot2i.template.Template

interface Factory<out T : Template> {
    @Throws(Exception::class)
    fun newTemplate(): T
}