package org.illegaller.ratabb.hishoot2i.provider

import android.content.Context
import android.net.Uri
import android.os.Environment
import common.FileConstants
import timber.log.Timber
import java.io.File
import java.io.IOException

class FileConstantsImpl(
    val context: Context
) : FileConstants {
    override fun savedDir(): File {
        @Suppress("DEPRECATION") // DEPRECATION = Environment.getExternalStorageDirectory()
        val parent: File = when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> Environment.getExternalStorageDirectory()
            else -> context.getExternalFilesDir(null) //
        }?.takeIf { it.canWrite() } ?: context.filesDir
        val result = File(parent, "HiShoot")
        if (!result.exists()) result.mkdirs()
        return result
    }

    override fun htzDir(): File {
        // NOTE: prepared external
        val parent = externalFilesDir() ?: context.filesDir
        val result = File(parent, "htz")
        if (!result.exists()) result.mkdirs()
        try {
            File(result, FileConstants.NO_MEDIA).createNewFile()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return result
    }

    override fun toUri(file: File): Uri = SavedStorageProvider.getUriForFile(context, file)

    override fun bgCrop(): File {
        val result = File(context.cacheDir, FileConstants.BG_CROP)
        if (result.exists()) result.delete()
        try {
            result.createNewFile()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return result
    }

    private fun externalFilesDir() = try {
        context.getExternalFilesDir(null)
    } catch (ignore: Exception) {
        null
    }
}
