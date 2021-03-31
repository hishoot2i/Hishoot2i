package template

import java.io.File
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * decompressHtz
 *
 * [src] an archive file htz ex: `someFile.htz`
 *
 * [dst] directory file at application internal ex: `<htzDir>/<HtzId>/`
 *
 **/
fun decompressHtz(src: File, dst: File) {
    fun validateCanonical(relativePath: String, dst: File): String =
        File(dst, relativePath).canonicalPath.takeIf { it.startsWith(dst.canonicalPath) }
            ?: throw IllegalStateException("Entry tried to write outside destination")

    if (dst.exists()) dst.deleteRecursively() else dst.mkdirs()
    ZipInputStream(src.inputStream().buffered()).use { zis ->
        loopZis@ while (true) {
            val entry = zis.nextEntry ?: break@loopZis // end of zis
            val out = File(validateCanonical(entry.name, dst))
            if (entry.isDirectory) continue@loopZis // ignored -- Htz not support Dir inside it.
            else out.outputStream().buffered().use { zis.copyTo(it) }
        }
    }
}

/**
 * compressHtz
 *
 * [src] directory file at application internal ex: `<htzDir>/<HtzId>/`
 *
 * [dst] an archive file htz ex: `someFile.htz`
 *
 **/
fun compressHtz(src: File, dst: File) {
    val listFiles = src.listFiles(File::isFile) ?: emptyArray() //
    require(src.isDirectory && listFiles.isNotEmpty()) {
        "src must be Directory and have children file inside it."
    }
    ZipOutputStream(dst.outputStream().buffered()).use { zos ->
        listFiles.forEach { file ->
            val entryName = file.name
            zos.putNextEntry(ZipEntry(entryName))
            file.inputStream().buffered().use { it.copyTo(zos) }
            zos.closeEntry()
        }
        zos.flush()
    }
}

/** shortcut for combine [getEntry][ZipFile.getEntry] and [getInputStream][ZipFile.getInputStream]*/
fun ZipFile.entryInputStream(entryName: String): InputStream = getInputStream(getEntry(entryName))
