package org.illegaller.ratabb.hishoot2i.ui.template

import com.commonsware.cwac.security.ZipUtils
import common.FileConstants
import common.ext.entryInputStream
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.delayed
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.data.source.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import template.Template.VersionHtz
import template.TemplateComparator
import template.TemplateConstants
import template.TemplateFactoryManager
import template.reader.ModelHtzReader
import java.io.File
import java.util.zip.ZipFile
import javax.inject.Inject

class TemplatePresenterImpl @Inject constructor(
    private val templateDataSource: TemplateDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val templateToolPref: TemplateToolPref,
    private val templatePref: TemplatePref,
    fileConstants: FileConstants,
    templateFactoryManager: TemplateFactoryManager
) : TemplatePresenter, BasePresenter<TemplateView>() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val tempData = mutableListOf<Template>()
    private val htzDir: () -> File = (fileConstants::htzDir)
    private val versionHtz: (String, Long) -> VersionHtz =
        (templateFactoryManager::versionHtz)

    override fun detachView() {
        super.detachView()
        tempData.clear()
        disposables.clear()
    }

    override fun search(queryObservable: Observable<String>) {
        queryObservable.delayed()
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {
                requiredView().showProgress()
            }
            .subscribeBy(
                onError = { viewOnError(it) },
                onNext = { query: String ->
                    val filteredData = mutableListOf<Template>()
                    if (query.isEmpty()) {
                        filteredData.addAll(tempData)
                    } else {
                        tempData.filter {
                            it.containsNameOrAuthor(query)
                        }.also { filteredData.addAll(it) }
                    }
                    viewSetData(filteredData)
                }
            )
            .addTo(disposables)
    }

    override fun importHtz(htz: File) {
        // fail fast
        require(htz.extension == "htz") { "expected is Htz, but it's a ${htz.extension}" }
       // requiredView().showProgress()
        Single.fromCallable {
            ZipFile(htz)
                .entryInputStream(TemplateConstants.TEMPLATE_CFG)
                .use { ModelHtzReader(it).model() }
        }
            .map { it.generateTemplateId() }
            .flatMap { unzipAndBuild(it, htz) }
            .ioUI(schedulerProvider)
            .doOnSubscribe {
                requiredView().showProgress()
            }
            .subscribeBy(
                onError = { viewOnError(it) },
                onSuccess = { viewOnSuccessImportHtz(it) }
            )
            .addTo(disposables)
    }

    override var templateComparator: TemplateComparator
        get() = templatePref.templateComparator
        set(value) {
            templatePref.templateComparator = value
            render()
        }

    override fun render() {
        //requiredView().showProgress()
        tempData.clear()
        templateDataSource.allTemplate()
            .sorted(templatePref.templateComparator)
            .ioUI(schedulerProvider)
            .doOnSubscribe {
                requiredView().showProgress()
            }
            .subscribeBy(
                onError = { viewOnError(it) },
                onComplete = { viewSetData(tempData) },
                onNext = { tempData += it }
            )
            .addTo(disposables)
    }

    override fun setCurrentTemplate(template: Template): Boolean {
        if (templateToolPref.templateCurrentId != template.id) {
            templateToolPref.templateCurrentId = template.id
            return true
        }
        return false
    }

    private fun Template.containsNameOrAuthor(query: String): Boolean =
        name.contains(query, true) || author.contains(query, true)

    private fun viewOnError(e: Throwable) {
        requiredView().apply {
            onError(e)
            hideProgress()
        }
    }

    private fun viewSetData(data: List<Template>) {
        requiredView().apply {
            setData(data)
            hideProgress()
        }
    }

    private fun viewOnSuccessImportHtz(htz: VersionHtz) {
        requiredView().apply {
            htzImported(htz)
            hideProgress()
        }
        render() // ?
    }

    private fun unzipAndBuild(templateId: String, htz: File): Single<VersionHtz> =
        Single.fromCallable {
            val maxHtzEntries = 10 // Sample_htz = 4 {config,preview,overlay,frame}
            val maxHtzSize = 1024 * 1024 * 10 // 10Mb | Sample_htz = +-50Kb
            val dst = File(htzDir(), templateId)
            if (dst.exists()) ZipUtils.delete(dst) //
            ZipUtils.unzip(htz, dst, maxHtzEntries, maxHtzSize) //
            versionHtz(templateId, System.currentTimeMillis())
        }
}
