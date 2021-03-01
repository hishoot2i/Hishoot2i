package org.illegaller.ratabb.hishoot2i.ui.template

import common.FileConstants
import common.UnZipper
import common.ext.DEFAULT_DELAY_MS
import common.ext.entryInputStream
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
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
    private val templateSource: TemplateSource,
    private val templateToolPref: TemplateToolPref,
    private val templatePref: TemplatePref,
    fileConstants: FileConstants,
    templateFactoryManager: TemplateFactoryManager
) : TemplatePresenter, BasePresenter<TemplateView>() {

    private val tempDataForSearch = mutableListOf<Template>()
    private val htzDir: () -> File = (fileConstants::htzDir)
    private val versionHtz: (String, Long) -> VersionHtz =
        (templateFactoryManager::versionHtz)

    override fun detachView() {
        super.detachView()
        tempDataForSearch.clear()
    }

    @FlowPreview
    override fun search(queries: Flow<String>) {
        queries.debounce(DEFAULT_DELAY_MS)
            .onEach { query: String ->
                val filteredData = mutableListOf<Template>()
                if (query.isEmpty()) {
                    filteredData.addAll(tempDataForSearch)
                } else {
                    tempDataForSearch.filter {
                        it.containsNameOrAuthor(query)
                    }.also { filteredData.addAll(it) }
                }
                viewSetData(filteredData)
            }
            .catch { viewOnError(it) }
            .launchIn(this)
    }

    override fun importHtz(htz: File) {
        require(htz.extension == "htz") { "expected is Htz, but it's a ${htz.extension}" }
        launch {
            runCatching { withContext(IO) { unzipAndBuild(htz) } }
                .fold(::viewOnSuccessImportHtz, ::viewOnError)
        }
    }

    override var templateComparator: TemplateComparator
        get() = templatePref.templateComparator
        set(value) {
            templatePref.templateComparator = value
            render()
        }

    override fun render() {
        tempDataForSearch.clear()
        launch {
            requiredView().showProgress()
            runCatching {
                withContext(IO) {
                    templateSource.allTemplate().sortedWith(templateComparator)
                }.also { tempDataForSearch.addAll(it) }
            }.fold(::viewSetData, ::viewOnError)
        }
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

    private fun unzipAndBuild(htz: File): VersionHtz {
        val id = ZipFile(htz)
            .entryInputStream(TemplateConstants.TEMPLATE_CFG)
            .use { ModelHtzReader(it).model() }
            .generateTemplateId()
        UnZipper.unzip(htz, File(htzDir(), id))
        return versionHtz(id, System.currentTimeMillis())
    }
}
