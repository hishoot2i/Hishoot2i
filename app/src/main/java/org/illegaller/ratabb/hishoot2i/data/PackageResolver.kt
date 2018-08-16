package org.illegaller.ratabb.hishoot2i.data

import io.reactivex.Flowable

interface PackageResolver {
    fun installedTemplateLegacy(): Flowable<entity.AppInfo>
    fun installedTemplate(version: Int): Flowable<entity.AppInfo>
}
