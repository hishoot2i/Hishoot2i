package org.illegaller.ratabb.hishoot2i.data.source

interface FileFontSource {
    suspend fun fontPaths(): List<String>
}
