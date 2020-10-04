package org.illegaller.ratabb.hishoot2i.data.core

import entity.ImageSourcePath
import io.reactivex.Single
import template.Template

interface CoreProcess {
    fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result>
    fun save(template: Template, sourcePath: ImageSourcePath): Single<Result>
}
