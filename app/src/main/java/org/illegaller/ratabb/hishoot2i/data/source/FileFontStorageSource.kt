package org.illegaller.ratabb.hishoot2i.data.source

import io.reactivex.rxjava3.core.Flowable
import java.io.File

interface FileFontStorageSource {
    fun fileFonts(): Flowable<File>
}
