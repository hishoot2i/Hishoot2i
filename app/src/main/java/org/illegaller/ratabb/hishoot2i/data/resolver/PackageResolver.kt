package org.illegaller.ratabb.hishoot2i.data.resolver

import entity.AppInfo

interface PackageResolver {
    fun installedTemplateLegacy(): List<AppInfo>
    fun installedTemplate(version: Int): List<AppInfo>
}
