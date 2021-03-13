package org.illegaller.ratabb.hishoot2i.data.source

import android.os.Environment
import common.ext.isDirAndCanRead
import common.ext.listFilesByExt
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import java.io.File
import javax.inject.Inject

class FileFontSourceImpl @Inject constructor(
    private val settingPref: SettingPref
) : FileFontSource {

    override fun fontPaths(): List<String> =
        listOf("DEFAULT") + fileFonts().map(File::getAbsolutePath)

    private fun fileFonts(): List<File> =
        (defaultFontDir() + customFontDir() + systemFontDir()).filterNotNull()
            .filter(File::isDirAndCanRead)
            .map(::fontsByExt)
            .flatMap(Array<File>::asList)
            .distinctBy(File::nameWithoutExtension)
            .sortedBy(File::getName)

    private fun defaultFontDir(): List<File> =
        arrayOf("font", "fonts", "Font", "Fonts").map(::externalStorageDir)

    private fun customFontDir(): File? = settingPref.customFontPath?.let { File(it) }

    private fun systemFontDir(): File? =
        if (settingPref.systemFontEnable) File("/system/fonts") else null

    private fun fontsByExt(file: File): Array<File> = file.listFilesByExt("ttf", "otf")

    @Suppress("DEPRECATION")
    private fun externalStorageDir(path: String) =
        File(Environment.getExternalStorageDirectory(), path)
}
