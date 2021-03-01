package org.illegaller.ratabb.hishoot2i.data.source

import android.os.Environment
import common.ext.isDirAndCanRead
import common.ext.listFilesByExt
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import java.io.File
import javax.inject.Inject

class FileFontSourceImpl @Inject constructor(
    private val settingPref: SettingPref
) : FileFontSource {

    override suspend fun fileFonts(): List<File> = withContext(IO) {
        (defaultFontDir() + customFontDir() + systemFontDir()).filterNotNull()
            .filter { it.isDirAndCanRead() }
            .map { it.listFilesByExt("ttf", "otf") }
            .flatMap { it.asList() }
            .distinctBy { it.nameWithoutExtension }
            .sortedBy { it.name }
    }

    @Suppress("DEPRECATION") // DEPRECATION: getExternalStorageDirectory
    private fun defaultFontDir(): List<File> = arrayOf("font", "fonts", "Font", "Fonts")
        .map { File(Environment.getExternalStorageDirectory(), it) }

    private fun customFontDir(): File? = settingPref.customFontPath?.let { File(it) }

    private fun systemFontDir(): File? =
        if (settingPref.systemFontEnable) File("/system/fonts") else null
}
