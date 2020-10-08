package org.illegaller.ratabb.hishoot2i.data.source

import android.os.Environment
import common.ext.compareByName
import common.ext.isDirAndCanRead
import common.ext.listFilesByExt
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.mergeDelayError
import io.reactivex.rxjava3.kotlin.toFlowable
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import java.io.File
import javax.inject.Inject

class FileFontStorageSourceImpl @Inject constructor(
    private val settingPref: SettingPref
) : FileFontStorageSource {

    override fun fileFonts(): Flowable<File> =
        listOf(systemFontDir(), defaultFontDir(), customFontDir())
            .mergeDelayError()
            .filter { it.isDirAndCanRead() }
            .map { it.listFilesByExt("ttf", "otf") }
            .flatMap { it.toFlowable() }
            .distinct { it.nameWithoutExtension }
            .sorted { file, other -> file.compareByName(other) }

    @Suppress("DEPRECATION") // DEPRECATION: getExternalStorageDirectory
    private fun defaultFontDir(): Flowable<File> = arrayOf("font", "fonts", "Font", "Fonts")
        .toFlowable()
        .flatMap { path: String ->
            Flowable.fromCallable {
                File(Environment.getExternalStorageDirectory(), path)
            }
        }

    private fun customFontDir(): Flowable<File> = settingPref.customFontPath?.let { path: String ->
        Flowable.fromCallable { File(path) }
    } ?: Flowable.empty()

    private fun systemFontDir(): Flowable<File> = if (settingPref.systemFontEnable) {
        Flowable.fromCallable { File("/system/fonts") } // NOTE: hard-code system font file ?
    } else Flowable.empty()
}
