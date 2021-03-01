package org.illegaller.ratabb.hishoot2i.ui.main

import androidx.annotation.ColorInt
import common.ext.exhaustive
import core.CoreProcess
import core.CoreResult
import core.Preview
import core.Save
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
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import javax.inject.Inject

class MainPresenterImpl @Inject constructor(
    private val coreProcess: CoreProcess,
    private val templateSource: TemplateSource,
    private val backgroundToolPref: BackgroundToolPref,
    private val badgeToolPref: BadgeToolPref,
    private val screenToolPref: ScreenToolPref,
    private val templateToolPref: TemplateToolPref
) : MainPresenter, BasePresenter<MainView>() {
    private var lastTemplateId: String? = null
    private var lastTemplate: Template? = null
    private val isHaveLastTemplate: Boolean
        get() = lastTemplateId != null && lastTemplate != null &&
            lastTemplateId == templateToolPref.templateCurrentId

    @ExperimentalCoroutinesApi
    override fun attachView(view: MainView) {
        super.attachView(view)
        preferenceChangesSubscriber() //
    }

    override fun detachView() {
        lastTemplateId = null
        lastTemplate = null
        super.detachView()
    }

    // Raw Uri
    override val sourcePath = ImageSourcePath()

    /**/
    override fun resume() {
        lastTemplateId?.let {
            if (it != templateToolPref.templateCurrentId) {
                render()
            }
        }
    }

    private suspend fun currentTemplate(): Template = if (isHaveLastTemplate) {
        lastTemplate!!
    } else {
        templateSource.findByIdOrDefault(templateToolPref.templateCurrentId).also {
            lastTemplate = it
            lastTemplateId = it.id
        }
    }

    /**/
    override fun render() {
        launch {
            requiredView().showProgress()
            runCatching { withContext(IO) { coreProcess.preview(currentTemplate(), sourcePath) } }
                .fold(::viewOnResult, ::viewOnError)
        }
    }

    /**/
    override fun save() {
        launch {
            requiredView().showProgress()
            requiredView().startSave()
            runCatching { withContext(IO) { coreProcess.save(currentTemplate(), sourcePath) } }
                .fold(::viewOnResult, ::viewOnError)
        }
    }

    override fun backgroundColorPipette(@ColorInt color: Int) {
        if (backgroundToolPref.backgroundColorInt != color) {
            backgroundToolPref.backgroundColorInt = color
        }
    }

    override fun changeScreen1(path: String) {
        sourcePath.screen1 = path
        render()
    }

    override fun changeScreen2(path: String) {
        sourcePath.screen2 = path
        render()
    }

    override fun changeBackground(path: String) {
        sourcePath.background = path
        if (backgroundToolPref.backgroundMode.isImage) render()
        else backgroundToolPref.backgroundMode = BackgroundMode.IMAGE
    }

    @ExperimentalCoroutinesApi
    private fun preferenceChangesSubscriber() {
        (
            screenToolPref.mainFlow + badgeToolPref.mainFlow +
                templateToolPref.mainFlow + backgroundToolPref.mainFlow
            )
            .merge()
            .filter { it.isNotManualCrop() }
            .onEach { render() }
            .catch { viewOnError(it) }
            .launchIn(this)
    }

    private fun Any?.isNotManualCrop() = (this as? ImageOption)?.isManualCrop != true

    private fun viewOnError(throwable: Throwable) {
        with(requiredView()) {
            onError(throwable)
            hideProgress()
        }
    }

    private fun viewOnResult(coreResult: CoreResult) {
        with(requiredView()) {
            when (coreResult) {
                is Preview -> preview(coreResult.bitmap)
                is Save -> save(coreResult.bitmap, coreResult.uri, coreResult.name)
            }.exhaustive
            hideProgress()
        }
    }
}
