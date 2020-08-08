package org.illegaller.ratabb.hishoot2i.data.core

import io.reactivex.Single
import template.Template

interface CoreProcess {
    fun preview(template: Template, sourcePath: entity.ImageSourcePath): Single<Result>
    fun save(template: Template, sourcePath: entity.ImageSourcePath): Single<Result>
}
