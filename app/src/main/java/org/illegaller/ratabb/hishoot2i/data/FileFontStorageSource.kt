package org.illegaller.ratabb.hishoot2i.data

import io.reactivex.Flowable
import java.io.File

interface FileFontStorageSource {
    fun fileFonts(): Flowable<File>
}
