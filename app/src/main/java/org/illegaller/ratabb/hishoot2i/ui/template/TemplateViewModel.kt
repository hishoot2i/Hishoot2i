package org.illegaller.ratabb.hishoot2i.ui.template

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import template.Template
import template.Template.VersionHtz
import template.TemplateFactoryManager
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateSource: TemplateSource,
    private val templatePref: TemplatePref,
    private val manager: TemplateFactoryManager
) : ViewModel() {

    private val _uiState = MutableLiveData<TemplateView>()
    internal val uiState: LiveData<TemplateView>
        get() = _uiState

    private val _htzState = MutableLiveData<HtzEventView>()
    internal val htzState: LiveData<HtzEventView>
        get() = _htzState

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun search(queries: Flow<String>) {
        queries.debounce(600L)
            .distinctUntilChanged()
            .mapLatest {
                withContext(IO) {
                    templateSource.searchByNameOrAuthor(it)
                        .sortedWith(templatePref.templateComparator)
                }
            }
            .onEach { _uiState.value = Success(it) }
            .onCompletion { cause -> cause?.let { _uiState.value = Fail(it) } }
            .catch { _uiState.value = Fail(it) }
            .launchIn(viewModelScope)
    }

    fun perform() {
        viewModelScope.launch {
            _uiState.value = Loading
            runCatching {
                withContext(IO) {
                    templateSource.allTemplate().sortedWith(templatePref.templateComparator)
                }
            }.fold({ _uiState.value = Success(it) }, { _uiState.value = Fail(it) })
        }
    }

    fun importFileHtz(htz: File) {
        require(htz.extension == "htz") { "Expected is Htz, but it's a ${htz.extension}" }
        viewModelScope.launch {
            _htzState.value = LoadingHtzEvent
            runCatching {
                withContext(IO) { manager.importHtz(htz) }
            }.fold(
                {
                    _htzState.value = SuccessHtzEvent(HtzEvent.IMPORT, it.name)
                    perform()
                },
                { _htzState.value = FailHtzEvent(it) }
            )
        }
    }

    fun convertTemplateHtz(template: Template) {
        viewModelScope.launch {
            _htzState.value = LoadingHtzEvent
            runCatching {
                withContext(IO) { manager.convertHtz(template) }
            }.fold(
                {
                    _htzState.value = SuccessHtzEvent(HtzEvent.CONVERT, it.name)
                    perform()
                },
                { _htzState.value = FailHtzEvent(it) }
            )
        }
    }

    fun exportTemplateHtz(template: VersionHtz) {
        viewModelScope.launch {
            _htzState.value = LoadingHtzEvent
            runCatching {
                withContext(IO) { manager.exportHtz(template) }
            }.fold(
                {
                    _htzState.value = SuccessHtzEvent(HtzEvent.EXPORT, it.name)
                    perform()
                },
                { _htzState.value = FailHtzEvent(it) }
            )
        }
    }

    fun removeTemplateHtz(template: VersionHtz) {
        viewModelScope.launch {
            _htzState.value = LoadingHtzEvent
            runCatching {
                withContext(IO) { manager.removeHtz(template) }
            }.fold(
                {
                    _htzState.value = SuccessHtzEvent(HtzEvent.REMOVE, it)
                    perform()
                },
                { _htzState.value = FailHtzEvent(it) }
            )
        }
    }
}
