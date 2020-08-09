/*
 * Copyright (c) 2015 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.commonsware.cwac.security

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

// inspired by https://www.securecoding.cert.org/confluence/display/java/IDS04-J.+Safely+extract+files+from+ZipInputStream
/**
 * Utilities for safely unzipping ZIP-style archives.
 */
object ZipUtils {
    private const val BUFFER_SIZE = 16384
    private const val DEFAULT_MAX_ENTRIES = 1024
    private const val DEFAULT_MAX_SIZE = 1024 * 1024 * 64
    /**
     * Unzips a ZIP-style archive to the designated directory.
     * Reproduces entire directory tree from the ZIP archive.
     * Entire operation fails if ZIP file is invalid (attempts
     * to write outside of designated directory, too big, or
     * too many entries). The "too big" and "too many entries"
     * rules are hard-coded in this method; use the four-parameter
     * unzip() method to configure those.
     *
     * If an UnzipException is thrown, anything previously written
     * to the designated directory is deleted, as is the designated
     * directory itself.
     *
     * @param zipFile the ZIP archive to unzip
     * @param destDir the directory to unzip the contents to
     * @param maxEntries throw an UnzipException if the ZIP
     * archive contains more than this number
     * of entries
     * @param maxSize throw an UnzipException if the sum of the
     * sizes of the entries in the ZIP archive is
     * bigger than this size in bytes
     * @throws UnzipException if something goes haywire
     */
    /**
     * Unzips a ZIP-style archive to the designated directory.
     * Reproduces entire directory tree from the ZIP archive.
     * Entire operation fails if ZIP file is invalid (attempts
     * to write outside of designated directory, too big, or
     * too many entries). The "too big" and "too many entries"
     * rules are hard-coded in this method; use the four-parameter
     * unzip() method to configure those.
     *
     * If an UnzipException is thrown, anything previously written
     * to the designated directory is deleted, as is the designated
     * directory itself.
     *
     * @param zipFile the ZIP archive to unzip
     * @param destDir the directory to unzip the contents to
     * @throws UnzipException if something goes haywire
     */
    @JvmOverloads
    @Throws(UnzipException::class, IOException::class)
    fun unzip(
        zipFile: File,
        destDir: File,
        maxEntries: Int = DEFAULT_MAX_ENTRIES,
        maxSize: Int = DEFAULT_MAX_SIZE
    ) {
        if (destDir.exists()) {
            if (destDir.list()?.isNotEmpty() != false) {
                throw IOException("Your destination directory is not empty!")
            }
        } else {
            destDir.mkdirs()
        }
        try {
            val fis = FileInputStream(zipFile)
            val zis = ZipInputStream(BufferedInputStream(fis))
            var entry: ZipEntry
            var entries = 0
            var total: Long = 0
            zis.use { it ->
                while (it.nextEntry.also { entry = it } != null) {
                    var bytesRead: Int = 0 //
                    val data = ByteArray(BUFFER_SIZE)
                    val zipCanonicalPath = validateZipEntry(entry.name, destDir)
                    if (entry.isDirectory) {
                        File(zipCanonicalPath).mkdirs()
                    } else {
                        File(zipCanonicalPath).parentFile?.mkdirs()
                        val fos = FileOutputStream(zipCanonicalPath)
                        val dest = BufferedOutputStream(fos, BUFFER_SIZE)
                        while (total + BUFFER_SIZE <= maxSize &&
                            it.read(data, 0, BUFFER_SIZE)
                                .also { bytesRead = it } != -1
                        ) {
                            dest.write(data, 0, bytesRead)
                            total += bytesRead.toLong()
                        }
                        dest.flush()
                        fos.fd.sync()
                        dest.close()
                        check(total + BUFFER_SIZE <= maxSize) { "Too much output from ZIP" }
                    }
                    it.closeEntry()
                    entries++
                    check(entries <= maxEntries) { "Too many entries in ZIP" }
                }
            }
        } catch (t: Throwable) {
            if (destDir.exists()) {
                delete(destDir)
            }
            throw UnzipException("Problem in unzip operation, rolling back", t)
        }
    }
    // inspired by http://pastebin.com/PqJyzQUx
    /**
     * Recursively deletes a directory and its contents.
     *
     * @param f The directory (or file) to delete
     * @return true if the delete succeeded, false otherwise
     */
    fun delete(f: File): Boolean {
        if (f.isDirectory) {
            f.listFiles()?.forEach { child ->
                if (!delete(child)) {
                    return false
                }
            }
        }
        return f.delete()
    }

    @Throws(IOException::class)
    private fun validateZipEntry(
        zipEntryRelativePath: String,
        destDir: File
    ): String {
        val zipEntryTarget = File(destDir, zipEntryRelativePath)
        val zipCanonicalPath = zipEntryTarget.canonicalPath
        if (zipCanonicalPath.startsWith(destDir.canonicalPath)) {
            return zipCanonicalPath
        }
        throw IllegalStateException("ZIP entry tried to write outside destination directory")
    }

    /**
     * Exception raised if something goes wrong in the unzip
     * work. Use getCause() to examine the underlying exception
     * (e.g., IllegalStateException).
     */
    class UnzipException(
        detailMessage: String?,
        throwable: Throwable?
    ) : Exception(detailMessage, throwable)
}
