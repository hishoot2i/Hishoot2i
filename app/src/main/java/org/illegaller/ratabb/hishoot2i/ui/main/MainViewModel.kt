package org.illegaller.ratabb.hishoot2i.ui.main

import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.CoreProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import entity.BackgroundMode
import entity.ImageOption
import entity.ImageSourcePath
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import org.illegaller.ratabb.hishoot2i.ui.ARG_BACKGROUND_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN1_PATH
import org.illegaller.ratabb.hishoot2i.ui.ARG_SCREEN2_PATH
import template.Template
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val coreProcess: CoreProcess,
    private val templateSource: TemplateSource,
    private val backgroundToolPref: BackgroundToolPref,
    private val badgeToolPref: BadgeToolPref,
    private val screenToolPref: ScreenToolPref,
    private val templateToolPref: TemplateToolPref,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var lastTemplateId: String? = null
    private var lastTemplate: Template? = null
    private val isHaveLastTemplate: Boolean
        get() = lastTemplateId != null && lastTemplate != null &&
            lastTemplateId == templateToolPref.templateCurrentId

    private val _uiState = MutableLiveData<MainView>()
    internal val uiState: LiveData<MainView>
        get() = _uiState

    private val sourcePath = ImageSourcePath()

    init {
        preferenceChanges() //
        sourcePath.background = savedStateHandle.get(ARG_BACKGROUND_PATH)
        sourcePath.screen1 = savedStateHandle.get(ARG_SCREEN1_PATH)
        sourcePath.screen2 = savedStateHandle.get(ARG_SCREEN2_PATH)
    }

    override fun onCleared() {
        lastTemplate = null
        lastTemplateId = null
    }

    fun resume() {
        lastTemplateId?.let {
            if (it != templateToolPref.templateCurrentId) {
                render()
            }
        }
    }

    private fun currentTemplate(): Template = if (isHaveLastTemplate) lastTemplate!! else {
        templateSource.findByIdOrDefault(templateToolPref.templateCurrentId).also {
            lastTemplate = it
            lastTemplateId = it.id
        }
    }

    fun render() {
        viewModelScope.launch {
            _uiState.value = Loading(false)
            runCatching { withContext(IO) { coreProcess.preview(currentTemplate(), sourcePath) } }
                .fold({ _uiState.value = Success(it) }, { _uiState.value = Fail(it, false) })
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value = Loading(true)
            runCatching { withContext(IO) { coreProcess.save(currentTemplate(), sourcePath) } }
                .fold({ _uiState.value = Success(it) }, { _uiState.value = Fail(it, true) })
        }
    }

    fun backgroundColorPipette(@ColorInt color: Int) {
        if (backgroundToolPref.backgroundColorInt != color) {
            backgroundToolPref.backgroundColorInt = color
        }
    }

    fun changeScreen1(path: String?) {
        if (path == null) return
        sourcePath.screen1 = path
        savedStateHandle.set(ARG_SCREEN1_PATH, path)
        render()
    }

    fun changeScreen2(path: String?) {
        if (path == null) return
        sourcePath.screen2 = path
        savedStateHandle.set(ARG_SCREEN2_PATH, path)
        render()
    }

    fun changeBackground(path: String?) {
        if (path == null) return
        sourcePath.background = path
        savedStateHandle.set(ARG_BACKGROUND_PATH, path)
        if (backgroundToolPref.backgroundMode.isImage) render()
        else backgroundToolPref.backgroundMode = BackgroundMode.IMAGE
    }

    @ExperimentalCoroutinesApi
    private fun preferenceChanges() {
        (
            screenToolPref.mainFlow + badgeToolPref.mainFlow +
                templateToolPref.mainFlow + backgroundToolPref.mainFlow
            )
            .merge()
            .filter { (it as? ImageOption)?.isManualCrop != true }
            .onEach { render() }
            .catch { _uiState.value = Fail(it, false) }
            .launchIn(viewModelScope)
    }
}
