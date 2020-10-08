package org.illegaller.ratabb.hishoot2i.data

import entity.AppInfo
import io.reactivex.rxjava3.core.Flowable

interface PackageResolver {
    fun installedTemplateLegacy(): Flowable<AppInfo>
    fun installedTemplate(version: Int): Flowable<AppInfo>
}
