package org.illegaller.ratabb.hishoot2i.data.source

import io.reactivex.Flowable
import io.reactivex.Single
import template.Template

interface TemplateDataSource {
    fun allTemplate(): Flowable<Template>
    fun findById(id: String): Single<Template>
}
