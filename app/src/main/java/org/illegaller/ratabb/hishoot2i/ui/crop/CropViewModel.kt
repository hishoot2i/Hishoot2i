package org.illegaller.ratabb.hishoot2i.ui.crop

import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.FileConstants
import common.ext.graphics.saveTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    fileConstants: FileConstants
) : ViewModel() {
    private val fileCrop: () -> File = (fileConstants::bgCrop)
    private val _uiState = MutableLiveData<CropView>()
    internal val uiState: LiveData<CropView>
        get() = _uiState

    fun savingCrop(bitmap: Bitmap) {
        viewModelScope.launch {
            runCatching {
                withContext(IO) { savingProcess(fileCrop(), bitmap) }
            }.fold({ _uiState.value = Success(it) }, { _uiState.value = Fail(it) })
        }
    }

    private fun savingProcess(file: File, bitmap: Bitmap): String {
        if (file.exists()) file.delete()
        file.createNewFile() //
        bitmap.saveTo(file)
        return file.toUri().toString()
    }
}
