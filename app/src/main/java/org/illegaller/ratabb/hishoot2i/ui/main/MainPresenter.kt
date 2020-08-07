package org.illegaller.ratabb.hishoot2i.ui.main

import androidx.annotation.ColorInt
import common.ext.exhaustive
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.mergeDelayError
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.Result
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.pref.asFlowable
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.computationUI
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val coreProcess: CoreProcess,
    private val templateSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val appPref: AppPref
) : BasePresenter<MainView>() {
    private val disposables: CompositeDisposable = CompositeDisposable()

    //
    internal val sourcePath = entity.ImageSourcePath()
    private var lastTemplateId: String? = null
    private var lastTemplate: Template? = null

    //
    private val isHaveLastTemplate: Boolean
        get() = lastTemplateId != null && lastTemplate != null &&
                lastTemplateId == appPref.templateCurrentId
    private val currentTemplate: Single<Template>
        get() = if (isHaveLastTemplate) Single.just(lastTemplate)
        else templateSource.findById(appPref.templateCurrentId)
            .subscribeOn(schedulerProvider.io())
            .doOnSuccess {
                lastTemplate = it
                lastTemplateId = it.id
            }

    /**/
    override fun attachView(view: MainView) {
        super.attachView(view)
        preferenceChangesSubscriber() //
    }

    /**/
    override fun detachView() {
        disposables.clear()
        lastTemplateId = null
        lastTemplate = null
        super.detachView()
    }

    /**/
    fun resume() {
        lastTemplateId?.let {
            if (it != appPref.templateCurrentId) {
                onPreview()
            }
        }
    }

    /**/
    fun onPreview() {
        view?.showProgress()
        currentTemplate.flatMap { coreProcess.preview(it, sourcePath) }
            .computationUI(schedulerProvider)
            .subscribeBy(::viewOnError, ::viewOnResult)
            .addTo(disposables)
    }

    /**/
    fun onSave() {
        view?.apply { showProgress(); startSave() }
        currentTemplate.flatMap { coreProcess.save(it, sourcePath) }
            .computationUI(schedulerProvider)
            .subscribeBy(::viewOnError, ::viewOnResult)
            .addTo(disposables)
    }

    fun setBackgroundColorFromPipette(@ColorInt color: Int) {
        if (appPref.backgroundColorInt != color) {
            // NOTE: preferenceChangeSubscriber -> onPreview
            appPref.backgroundColorInt = color
        }
    }

    fun changeScreen1(path: String) {
        sourcePath.apply { screen1 = path }
        onPreview()
    }

    fun changeScreen2(path: String) {
        sourcePath.apply { screen2 = path }
        onPreview()
    }

    fun changeBackground(path: String) {
        sourcePath.apply { background = path }
        val mode = entity.BackgroundMode.fromId(appPref.backgroundModeId)
        if (mode.isImage) onPreview() else appPref.backgroundModeId = entity.BackgroundMode.Image.id
    }

    private fun preferenceChangesSubscriber() {
        with(appPref) {
            arrayOf(
                asFlowable(::backgroundModeId),
                asFlowable(::backgroundColorInt),
                asFlowable(::backgroundImageBlurEnable),
                asFlowable(::backgroundImageBlurRadius),
                asFlowable(::doubleScreenEnable),
                asFlowable(::templateFrameEnable),
                asFlowable(::templateGlareEnable),
                asFlowable(::templateShadowEnable),
                asFlowable(::badgeColor),
                asFlowable(::badgeEnable),
                asFlowable(::badgePositionId),
                asFlowable(::badgeSize),
                asFlowable(::badgeText),
                asFlowable(::badgeTypeface)
            )
        }
            .asIterable()
            .mergeDelayError()
            .ioUI(schedulerProvider)
            .subscribeBy(::viewOnError) { onPreview() }
            .addTo(disposables)

        appPref.asFlowable(appPref::backgroundImageOptionId)
            .ioUI(schedulerProvider)
            .subscribeBy(::viewOnError) { imageOptionId: Int ->
                // NOTE: `manual crop` excluded.
                if (imageOptionId != R.id.toolBackgroundImageOptionManualCrop) onPreview()
            }
            .addTo(disposables)
    }

    private fun viewOnError(throwable: Throwable) {
        view?.apply { onError(throwable); hideProgress() }
    }

    private fun viewOnResult(result: Result) {
        view?.apply {
            when (result) {
                is Result.Preview -> preview(result.bitmap)
                is Result.Save -> save(result.bitmap, result.uri)
            }.exhaustive
            hideProgress()
        }
    }
}