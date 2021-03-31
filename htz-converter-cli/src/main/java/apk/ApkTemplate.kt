package apk

import com.manifest.data.MfFile
import com.manifest.data.StartTagChunk
import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import java.io.InputStream

class ApkTemplate private constructor(
    val packageName: String,
    val templateVersion: Int
) {
    companion object {
        private val mfFile = MfFile()

        @JvmStatic
        @JvmName("parse")
        fun InputStream.toApkTemplate(): ApkTemplate {
            use { mfFile.parse(this) }
            //
            val packageName = mfFile.startTagChunks.find {
                it.nameStr == "manifest"
            }?.attributes?.find {
                it.nameStr == "package"
            }?.valueStringStr ?: throw IllegalStateException("package not found")

            val isVersion1: Boolean = mfFile.startTagChunks.find { it.isVersion1() } != null

            if (isVersion1) return ApkTemplate(packageName, 1)

            val meta: StartTagChunk = mfFile.startTagChunks.find {
                it.nameStr == "meta-data" && it.attributes.find {
                    it.valueStringStr == META_DATA_TEMPLATE
                } != null
            } ?: throw IllegalStateException("Not Apk Template = $packageName")

            val version: Int = meta.attributes.find { it.nameStr == "value" }?.data?.toInt()
                ?: throw IllegalStateException("Not Apk Template = $packageName")

            return ApkTemplate(packageName, version) //
        }

        private fun StartTagChunk.isVersion1(): Boolean = nameStr == "category" &&
            attributes.find { it.nameStr == "name" }?.valueStringStr == CATEGORY_TEMPLATE_APK
    }
}
