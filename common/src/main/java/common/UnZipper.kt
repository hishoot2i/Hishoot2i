package common

import java.io.File
import java.util.zip.ZipInputStream

object UnZipper {

    private fun validateCanonical(
        relativePath: String,
        dst: File
    ): String = File(dst, relativePath).canonicalPath.takeIf {
        it.startsWith(dst.canonicalPath)
    } ?: throw IllegalStateException("ZIP entry tried to write outside destination directory")

    @JvmStatic
    fun unzip(src: File, dst: File) {
        if (dst.exists()) dst.deleteRecursively() else dst.mkdirs()
        ZipInputStream(src.inputStream().buffered()).use { zis ->
            loopZis@ while (true) {
                val entry = zis.nextEntry ?: break@loopZis
                val out = File(validateCanonical(entry.name, dst))
                if (entry.isDirectory) {
                    out.mkdirs()
                    continue@loopZis
                } else {
                    out.outputStream().buffered().use { zis.copyTo(it) }
                }
            }
        }
    }
}
