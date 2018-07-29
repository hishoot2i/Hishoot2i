package org.illegaller.ratabb.hishoot2i.data.core

import io.reactivex.Single
import rbb.hishoot2i.common.entity.ImageSourcePath
import rbb.hishoot2i.template.Template

interface CoreProcess {
    fun preview(template: Template, sourcePath: ImageSourcePath): Single<Result.Preview>
    fun save(template: Template, sourcePath: ImageSourcePath): Single<Result.Save>
}