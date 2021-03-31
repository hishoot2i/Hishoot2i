package apk

import com.manifest.data.MfFile
import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import java.io.InputStream

class ApkTemplate private constructor(
    val packageName: String,
    val templateVersion: Int
) {
    companion object {

        @JvmStatic
        @JvmName("parse")
        fun InputStream.toApkTemplate(): ApkTemplate {
            val parser = MfFile()
            use { parser.parse(this) }
            //
            val packageName = parser.packageName()
            return when {
                parser.isTemplateV1() -> ApkTemplate(packageName, 1)
                else -> ApkTemplate(packageName, parser.version())
            } //
        }

        private fun MfFile.packageName(): String = startTagChunks.find { chunk ->
            chunk.nameStr == "manifest"
        }?.attributes?.find { entry ->
            entry.nameStr == "package"
        }?.valueStringStr ?: throw IllegalStateException("package not found")

        private fun MfFile.isTemplateV1(): Boolean = startTagChunks.find { chunk ->
            chunk.nameStr == "category" && chunk.attributes.find { entry ->
                entry.nameStr == "name"
            }?.valueStringStr == CATEGORY_TEMPLATE_APK
        } != null

        private fun MfFile.version(): Int = startTagChunks.find { chunk ->
            chunk.nameStr == "meta-data" && chunk.attributes.find { entry ->
                entry.nameStr == "name"
            }?.valueStringStr == META_DATA_TEMPLATE
        }?.attributes?.find { entry ->
            entry.nameStr == "value"
        }?.data?.toInt() ?: throw IllegalStateException("Not Apk Template ")
    }
}
