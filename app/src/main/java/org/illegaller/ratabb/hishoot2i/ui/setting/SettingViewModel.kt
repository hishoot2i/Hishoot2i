package org.illegaller.ratabb.hishoot2i.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import imageloader.ImageLoader
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val imageLoader: ImageLoader
) : ViewModel() {

    private val _diskCacheSize = MutableLiveData(0L)
    val diskCacheSize: LiveData<Long>
        get() = _diskCacheSize

    init {
        viewModelScope.launch {
            withContext(IO) {
                _diskCacheSize.postValue(imageLoader.totalDiskCacheSize())
            }
        }
    }

    fun clearDiskCache() {
        viewModelScope.launch {
            withContext(IO) {
                imageLoader.clearDiskCache()
                _diskCacheSize.postValue(imageLoader.totalDiskCacheSize())
            }
        }
    }
}
