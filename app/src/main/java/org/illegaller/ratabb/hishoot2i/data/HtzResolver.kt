package org.illegaller.ratabb.hishoot2i.data

import io.reactivex.rxjava3.core.Flowable
import java.io.File

interface HtzResolver {
    fun installedHtz(): Flowable<File>
}
