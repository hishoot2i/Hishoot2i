package org.illegaller.ratabb.hishoot2i.ui.template

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.FileConstants
import common.UnZipper
import common.ext.DEFAULT_DELAY_MS
import common.ext.entryInputStream
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
import template.Template.VersionHtz
import template.TemplateConstants
import template.TemplateFactoryManager
import template.reader.ModelHtzReader
import java.io.File
import java.util.zip.ZipFile
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateSource: TemplateSource,
    private val templatePref: TemplatePref,
    fileConstants: FileConstants,
    templateFactoryManager: TemplateFactoryManager
) : ViewModel() {
    private val htzDir: () -> File = (fileConstants::htzDir)
    private val versionHtz: (String, Long) -> VersionHtz =
        (templateFactoryManager::versionHtz)

    private val _uiState = MutableLiveData<TemplateView>()
    internal val uiState: LiveData<TemplateView>
        get() = _uiState

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun search(queries: Flow<String>) {
        queries.debounce(DEFAULT_DELAY_MS)
            .distinctUntilChanged()
            .mapLatest {
                withContext(IO) {
                    templateSource.searchByNameOrAuthor(it)
                        .sortedWith(templatePref.templateComparator)
                }
            }
            .onEach { _uiState.value = Success(it) }
            .onCompletion { cause: Throwable? -> cause?.let { _uiState.value = Fail(it) } }
            .catch { _uiState.value = Fail(it) }
            .launchIn(viewModelScope)
    }

    fun importHtz(htz: File) {
        require(htz.extension == "htz") { "Expected is Htz, but it's a ${htz.extension}" }
        viewModelScope.launch {
            _uiState.value = Loading
            runCatching { withContext(IO) { unzipAndBuild(htz) } }
                .fold({ _uiState.value = HtzImported(it) }, { _uiState.value = Fail(it) })
        }
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

    private fun unzipAndBuild(htz: File): VersionHtz {
        val id = ZipFile(htz)
            .entryInputStream(TemplateConstants.TEMPLATE_CFG)
            .use { ModelHtzReader(it).model() }
            .generateTemplateId()
        UnZipper.unzip(htz, File(htzDir(), id))
        return versionHtz(id, System.currentTimeMillis())
    }
}
