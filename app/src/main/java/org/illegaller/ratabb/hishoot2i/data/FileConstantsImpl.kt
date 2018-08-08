package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import org.illegaller.ratabb.hishoot2i.BuildConfig
import rbb.hishoot2i.common.FileConstants
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FileConstantsImpl @Inject constructor(val context: Context) : FileConstants {
    private var saved: File? = null
    private var htz: File? = null
    private var bgCrop: File? = null
    override fun savedDir(): File {
        saved?.let { return it }
        val result = File(Environment.getExternalStorageDirectory(), "HiShoot")
        if (!result.exists()) {
            result.mkdirs()
        }
        saved = result
        return result
    }

    override fun htzDir(): File {
        htz?.let { return it }
        val result = File(context.filesDir, "htz")
        if (!result.exists()) {
            result.mkdirs()
        }
        try {
            File(result, FileConstants.NO_MEDIA).createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        htz = result
        return result
    }

    override fun File.toUri(): Uri =
        FileProvider.getUriForFile(context, BuildConfig.FILE_AUTHORITY, this)

    override fun bgCrop(): File {
        bgCrop?.let { return it }
        val result =
            File(context.cacheDir, FileConstants.BG_CROP)
        if (result.exists()) result.delete()
        result.createNewFile()
        bgCrop = result
        return result
    }
}