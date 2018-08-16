package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider.getUriForFile
import org.illegaller.ratabb.hishoot2i.BuildConfig.FILE_AUTHORITY
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FileConstantsImpl @Inject constructor(val context: Context) : common.FileConstants {
    override fun savedDir(): File {
        val parent = Environment.getExternalStorageState().let { state ->
            when (state) {
                Environment.MEDIA_MOUNTED -> Environment.getExternalStorageDirectory()
                else -> context.getExternalFilesDir(null)
            }.let { if (!it.canWrite()) context.filesDir else it }
        }
        val result = File(parent, "HiShoot")
        if (!result.exists()) result.mkdirs()
        return result
    }

    override fun htzDir(): File {
        val result = File(context.filesDir, "htz")
        if (!result.exists()) result.mkdirs()
        try {
            File(result, common.FileConstants.NO_MEDIA).createNewFile()
        } catch (ignore: IOException) {
        }
        return result
    }

    override fun File.toUri(): Uri = getUriForFile(context, FILE_AUTHORITY, this)
    override fun bgCrop(): File {
        val result = File(context.cacheDir, common.FileConstants.BG_CROP)
        if (result.exists()) result.delete()
        try {
            result.createNewFile()
        } catch (e: IOException) {
        }
        return result
    }
}