package org.illegaller.ratabb.hishoot2i.ui.template

import com.commonsware.cwac.security.ZipUtils
import common.FileConstants
import common.ext.entryInputStream
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import org.illegaller.ratabb.hishoot2i.data.rx.ioUI
import org.illegaller.ratabb.hishoot2i.ui.common.BasePresenter
import template.Template
import template.TemplateConstants.TEMPLATE_CFG
import template.TemplateFactoryManager
import template.model.ModelHtz
import template.reader.ModelHtzReader
import java.io.File
import java.util.Locale
import java.util.zip.ZipFile
import javax.inject.Inject

class TemplateManagerPresenter @Inject constructor(
    private val schedulerProvider: SchedulerProvider,
    private val templateFactoryManager: TemplateFactoryManager,
    private val fileConstants: FileConstants
) : BasePresenter<TemplateManagerView>() {
    private val disposable = CompositeDisposable()
    override fun detachView() {
        disposable.clear()
        super.detachView()
    }

    fun importHtz(htz: File?) {
        Single.fromCallable<ModelHtz> {
            if (!htz?.extension.equals("htz", ignoreCase = true)) {
                throw IllegalStateException("Not Htz")
            }
            ZipFile(htz).use { zipFile ->
                zipFile.entryInputStream(TEMPLATE_CFG).use { inputStream ->
                    ModelHtzReader(inputStream).use { it.model() }
                }
            }
        }
            .map(::generateTemplateId)
            .flatMap { unzipAndBuild(it, htz) }
            .ioUI(schedulerProvider)
            .subscribeBy({ view?.onError(it) }, { view?.onSuccessImportHtz(it) })
            .addTo(disposable)
    }

    private fun unzipAndBuild(templateId: String, htz: File?): Single<Template.VersionHtz> =
        Single.fromCallable<Template.VersionHtz> {
            val dst = File(fileConstants.htzDir(), templateId)
            if (dst.exists()) ZipUtils.delete(dst) //
            if (htz == null) throw IllegalStateException("htz == null") //
            ZipUtils.unzip(htz, dst, MAX_HTZ_ENTRIES, MAX_HTZ_SIZE) //
            templateFactoryManager.versionHtz(templateId, System.currentTimeMillis())
        }

    /* String for installed path and template Id. */
    private fun generateTemplateId(htzModel: ModelHtz): String = with(htzModel) {
        var ret = "${author.hashCode()}_${name.toLowerCase(Locale.ROOT)}"
            .replace("[^\\w]".toRegex(), replacement = "") // removing non word char
            .trim()
        val maxLength = 32
        if (ret.length > maxLength) ret = ret.substring(range = 0..maxLength) // limit
        ret
    }

    companion object {
        private const val MAX_HTZ_ENTRIES = 10 // Sample_htz = 4 {config,preview,overlay,frame}
        private const val MAX_HTZ_SIZE = 1024 * 1024 * 10 // 10Mb | Sample_htz = +-50Kb
    }
}
