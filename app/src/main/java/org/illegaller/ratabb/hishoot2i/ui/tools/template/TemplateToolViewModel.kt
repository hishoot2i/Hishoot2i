package org.illegaller.ratabb.hishoot2i.ui.tools.template

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import javax.inject.Inject

@HiltViewModel
class TemplateToolViewModel @Inject constructor(
    templateSource: TemplateSource,
    pref: TemplateToolPref
) : ViewModel() {
    private val _uiState = MutableLiveData<TemplateToolView>()
    internal val uiState: LiveData<TemplateToolView>
        get() = _uiState

    init {
        viewModelScope.launch {
            runCatching {
                withContext(IO) { templateSource.findByIdOrDefault(pref.templateCurrentId) }
            }.fold({ _uiState.value = Success(it, pref) }, { _uiState.value = Fail(it) })
        }
    }
}
