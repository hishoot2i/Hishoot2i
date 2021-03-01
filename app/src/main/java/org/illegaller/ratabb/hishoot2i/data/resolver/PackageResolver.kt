package org.illegaller.ratabb.hishoot2i.data.resolver

import entity.AppInfo

interface PackageResolver {
    suspend fun installedTemplateLegacy(): List<AppInfo>
    suspend fun installedTemplate(version: Int): List<AppInfo>
}
