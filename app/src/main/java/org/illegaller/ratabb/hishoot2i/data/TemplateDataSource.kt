package org.illegaller.ratabb.hishoot2i.data

import io.reactivex.Flowable
import io.reactivex.Single
import rbb.hishoot2i.template.Template

interface TemplateDataSource {
    fun allTemplate(): Flowable<Template>
    fun findById(id: String): Single<Template>
}