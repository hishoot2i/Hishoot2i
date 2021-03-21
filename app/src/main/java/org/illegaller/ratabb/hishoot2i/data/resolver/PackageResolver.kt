package org.illegaller.ratabb.hishoot2i.data.resolver

interface PackageResolver {
    fun installedTemplateLegacy(): List<Pair<String, Long>>
    fun installedTemplate(version: Int): List<Pair<String, Long>>
}
