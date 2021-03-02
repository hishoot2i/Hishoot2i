package org.illegaller.ratabb.hishoot2i.ui.tools.badge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSource
import javax.inject.Inject

@HiltViewModel
class BadgeToolViewModel @Inject constructor(
    fileFontSource: FileFontSource
) : ViewModel() {
    private val _uiState = MutableLiveData<BadgeView>()
    internal val uiState: LiveData<BadgeView>
        get() = _uiState

    init {
        viewModelScope.launch {
            runCatching { withContext(IO) { fileFontSource.fontPaths() } }
                .fold({ _uiState.value = Success(it) }, { _uiState.value = Fail(it) })
        }
    }
}
