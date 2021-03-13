package org.illegaller.ratabb.hishoot2i.data.source

import template.Template

interface TemplateSource {
    fun allTemplate(): List<Template>
    fun findByIdOrDefault(id: String): Template
    fun searchByNameOrAuthor(query: String): List<Template>
}
