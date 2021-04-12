package org.illegaller.ratabb.hishoot2i.data.source

import android.os.Environment
import common.graphics.DEFAULT_TYPEFACE_KEY
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import java.io.File
import javax.inject.Inject

class FileFontSourceImpl @Inject constructor(
    private val settingPref: SettingPref
) : FileFontSource {

    override fun fontPaths(): List<String> =
        listOf(DEFAULT_TYPEFACE_KEY) + fileFonts().map(File::getAbsolutePath)

    private fun fileFonts(): List<File> =
        (defaultFontDir() + customFontDir() + systemFontDir()).filterNotNull()
            .filter { it.isDirectory && it.canRead() }
            .map { file ->
                file.listFiles { child ->
                    child.extension in arrayOf("ttf", "otf")
                } ?: emptyArray()
            }
            .flatMap { it.asList() }
            .distinctBy { it.nameWithoutExtension }
            .sortedBy { it.name }

    private fun defaultFontDir(): List<File> =
        arrayOf("font", "fonts", "Font", "Fonts").map(::externalStorageDir)

    private fun customFontDir(): File? = settingPref.customFontPath?.let { File(it) }

    private fun systemFontDir(): File? =
        if (settingPref.systemFontEnable) File("/system/fonts") else null

    @Suppress("DEPRECATION")
    private fun externalStorageDir(path: String) =
        File(Environment.getExternalStorageDirectory(), path)
}
