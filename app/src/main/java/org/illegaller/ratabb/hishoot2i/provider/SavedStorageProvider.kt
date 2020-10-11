package org.illegaller.ratabb.hishoot2i.provider

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.illegaller.ratabb.hishoot2i.BuildConfig
import java.io.File

/** NOTE: Alias [FileProvider] for avoid duplication. */
class SavedStorageProvider : FileProvider() {
    companion object {
        @JvmStatic
        fun getUriForFile(context: Context, file: File): Uri =
            getUriForFile(context, BuildConfig.FILE_AUTHORITY, file)
    }
}
