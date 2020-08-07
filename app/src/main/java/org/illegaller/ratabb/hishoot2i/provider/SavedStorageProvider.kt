package org.illegaller.ratabb.hishoot2i.provider

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import org.illegaller.ratabb.hishoot2i.BuildConfig

/** NOTE: Alias [FileProvider] for avoid duplication. */
class SavedStorageProvider : FileProvider() {
    companion object {
        @JvmStatic
        fun getUriForFile(context: Context, file: File): Uri =
            FileProvider.getUriForFile(context, BuildConfig.FILE_AUTHORITY, file)
    }
}
