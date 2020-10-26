package org.illegaller.ratabb.hishoot2i.data.core.impl

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import common.FileConstants
import common.ext.graphics.saveTo
import common.ext.graphics.sizes
import common.ext.toDateTimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import entity.Sizes
import entity.ext
import entity.mimeType
import io.reactivex.rxjava3.core.Single
import org.illegaller.ratabb.hishoot2i.data.core.Result
import org.illegaller.ratabb.hishoot2i.data.core.SaveResult
import java.io.File
import javax.inject.Inject

class SaveResultImpl @Inject constructor(
    @ApplicationContext context: Context,
    fileConstants: FileConstants
) : SaveResult {
    private val resolver: ContentResolver by lazy { context.contentResolver }
    private val savedDir: () -> File = (fileConstants::savedDir)
    private val toUri: (File) -> Uri = (fileConstants::toUri)
    override fun save(
        bitmap: Bitmap,
        compressFormat: CompressFormat,
        saveQuality: Int
    ): Single<Result.Save> = Single.fromCallable {
        val nowMs = System.currentTimeMillis()
        @Suppress("SpellCheckingInspection") val fileName =
            "HiShoot_${nowMs.toDateTimeFormat("yyyyMMdd_HHmmss")}.${compressFormat.ext}"
        val file = File(savedDir(), fileName).also { file ->
            // Save File
            bitmap.saveTo(file, compressFormat, saveQuality)
        }
        val uri: Uri = contentValues(
            nowMs,
            bitmap.sizes,
            file,
            fileName,
            compressFormat.mimeType
        ).run {
            // Save MediaStore or [?:] *not* ;/
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this) ?: toUri(file) //
        }
        Result.Save(bitmap, uri, fileName)
    }

    private fun contentValues(
        nowMs: Long,
        bitmapSize: Sizes,
        file: File,
        fileName: String,
        mimeType: String
    ): ContentValues {
        val nowInSeconds = nowMs / 1000
        val (width, height) = bitmapSize
        return contentValuesOf(
            @Suppress("DEPRECATION") // DATA
            MediaStore.Images.ImageColumns.DATA to file.absolutePath,
            MediaStore.Images.ImageColumns.TITLE to fileName,
            MediaStore.Images.ImageColumns.DISPLAY_NAME to fileName,
            MediaStore.Images.ImageColumns.DATE_ADDED to nowInSeconds,
            MediaStore.Images.ImageColumns.DATE_MODIFIED to nowInSeconds,
            MediaStore.Images.ImageColumns.MIME_TYPE to mimeType,
            MediaStore.Images.ImageColumns.WIDTH to width,
            MediaStore.Images.ImageColumns.HEIGHT to height,
            MediaStore.Images.ImageColumns.SIZE to file.length()
        ).also {
            if (SDK_INT >= 29) it.put(MediaStore.Images.ImageColumns.DATE_TAKEN, nowMs)
        }
    }
}
