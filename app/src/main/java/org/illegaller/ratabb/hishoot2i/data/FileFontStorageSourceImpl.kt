package org.illegaller.ratabb.hishoot2i.data

import io.reactivex.Flowable
import io.reactivex.rxkotlin.mergeDelayError
import io.reactivex.rxkotlin.toFlowable
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import java.io.File
import java.text.Collator
import javax.inject.Inject
import android.os.Environment.getExternalStorageDirectory as ExternalStorageDir

class FileFontStorageSourceImpl @Inject constructor(val appPref: AppPref) : FileFontStorageSource {
    override fun fileFonts(): Flowable<File> = arrayOf(
        systemFontDir(),
        defaultFontDir(),
        customFontDir()
    )
        .asIterable()
        .mergeDelayError()
        .filter { it.canRead() && it.isDirectory }
        .flatMap {
            it.listFiles { file: File? -> SUPPORT_FONT_EXT.contains(file?.extension) }
                .toFlowable()
        }
        .distinct { it.nameWithoutExtension } //
        .sorted(::sortFileByNames)

    private fun defaultFontDir(): Flowable<File> = DEFAULT_FONT_PATH.toFlowable()
        .flatMap { path: String -> Flowable.fromCallable { File(ExternalStorageDir(), path) } }

    private fun customFontDir(): Flowable<File> = appPref.customFontPath?.let { path: String ->
        Flowable.fromCallable { File(path) }
    } ?: Flowable.empty()

    private fun systemFontDir(): Flowable<File> = if (appPref.systemFontEnable) {
        Flowable.fromCallable { File(SYSTEM_FONT_PATH) }
    } else Flowable.empty()

    private fun sortFileByNames(lhs: File, rhs: File): Int = collator.compare(lhs.name, rhs.name)

    companion object {
        private const val SYSTEM_FONT_PATH = "/system/fonts" //
        private val DEFAULT_FONT_PATH = arrayOf("font", "fonts")
        private val SUPPORT_FONT_EXT = arrayOf("ttf", "otf")
        private val collator = Collator.getInstance()
    }
}