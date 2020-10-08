package org.illegaller.ratabb.hishoot2i.data.source

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import template.Template

interface TemplateDataSource {
    fun allTemplate(): Flowable<Template>
    fun findById(id: String): Single<Template>
}
