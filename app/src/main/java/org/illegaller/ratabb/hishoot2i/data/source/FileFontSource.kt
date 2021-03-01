package org.illegaller.ratabb.hishoot2i.data.source

import java.io.File

interface FileFontSource {
    suspend fun fileFonts(): List<File>
}
