package org.illegaller.ratabb.hishoot2i.data.source

import template.Template

interface TemplateSource {
    suspend fun allTemplate(): List<Template>
    suspend fun findByIdOrDefault(id: String): Template
}
