package org.illegaller.ratabb.hishoot2i.ui.main

import androidx.annotation.ColorInt
import common.ext.exhaustive
import entity.BackgroundMode
import entity.ImageOption
import entity.ImageSourcePath
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.mergeDelayError
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.Result
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.computationUI
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.data.source.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import javax.inject.Inject

class MainPresenterImpl @Inject constructor(
    private val coreProcess: CoreProcess,
    private val templateSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val backgroundToolPref: BackgroundToolPref,
    private val badgeToolPref: BadgeToolPref,
    private val screenToolPref: ScreenToolPref,
    private val templateToolPref: TemplateToolPref
) : MainPresenter, BasePresenter<MainView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var lastTemplateId: String? = null
    private var lastTemplate: Template? = null
    private val isHaveLastTemplate: Boolean
        get() = lastTemplateId != null && lastTemplate != null &&
            lastTemplateId == templateToolPref.templateCurrentId

    private val currentTemplate: Single<Template>
        get() = if (isHaveLastTemplate) Single.just(lastTemplate)
        else templateSource.findById(templateToolPref.templateCurrentId)
            .subscribeOn(schedulerProvider.io())
            .doOnSuccess {
                lastTemplate = it
                lastTemplateId = it.id
            }

    override fun attachView(view: MainView) {
        super.attachView(view)
        preferenceChangesSubscriber() //
    }

    override fun detachView() {
        disposables.clear()
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

    /**/
    override fun render() {
        requiredView().showProgress()
        currentTemplate.flatMap { coreProcess.preview(it, sourcePath) }
            .computationUI(schedulerProvider)
            .subscribeBy(::viewOnError, ::viewOnResult)
            .addTo(disposables)
    }

    /**/
    override fun save() {
        with(requiredView()) {
            showProgress()
            startSave()
        }
        currentTemplate.flatMap { coreProcess.save(it, sourcePath) }
            .computationUI(schedulerProvider)
            .subscribeBy(::viewOnError, ::viewOnResult)
            .addTo(disposables)
    }

    override fun backgroundColorPipette(@ColorInt color: Int) {
        if (backgroundToolPref.backgroundColorInt != color) {
            // NOTE: preferenceChangeSubscriber -> onPreview
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

    private fun preferenceChangesSubscriber() {
        (
            screenToolPref.mainFlow + badgeToolPref.mainFlow +
                templateToolPref.mainFlow + backgroundToolPref.mainFlow
            )
            .mergeDelayError()
            .ioUI(schedulerProvider)
            .subscribeBy(
                onError = { viewOnError(it) },
                onNext = { doOnPreviewIf(it.isNotManualCrop()) }
            )
            .addTo(disposables)
    }

    private fun doOnPreviewIf(condition: Boolean) {
        if (condition) render()
    }

    private fun Any?.isNotManualCrop() = (this as? ImageOption)?.isManualCrop != true

    private fun viewOnError(throwable: Throwable) {
        with(requiredView()) {
            onError(throwable)
            hideProgress()
        }
    }

    private fun viewOnResult(result: Result) {
        with(requiredView()) {
            when (result) {
                is Result.Preview -> preview(result.bitmap)
                is Result.Save -> save(result.bitmap, result.uri, result.name)
            }.exhaustive
            hideProgress()
        }
    }
}
