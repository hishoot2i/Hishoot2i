package apk

import template.TemplateConstants.CATEGORY_TEMPLATE_APK
import template.TemplateConstants.META_DATA_TEMPLATE
import java.io.InputStream

internal const val ANDROID_MANIFEST = "AndroidManifest.xml"

private const val END_DOC_TAG = 0x00100101
private const val START_TAG = 0x00100102
private const val END_TAG = 0x00100103

@JvmName("parseBinaryXml")
internal fun InputStream.binaryXmlManifest(): Pair<String, Int> =
    buffered().use(InputStream::readBytes).parse()

private fun ByteArray.parse(): Pair<String, Int> {
    var offset = xmlTagOffset()
    var packageName: String? = null
    var templateVersion: Int = -1
    loopTag@ while (offset < size) {
        val tag = this lew offset
        val nameSi = this lew offset + 5 * 4
        when (tag) {
            START_TAG -> {
                val numbAttrs = this lew offset + 7 * 4
                offset += 9 * 4 //
                val name = this xmlString nameSi
                var hasMetaTemplate = false
                loopAttrs@ for (index in 0 until numbAttrs) {
                    val attrNameSi = this lew offset + 1 * 4
                    val attrValueSi = this lew offset + 2 * 4
                    val attrResId = this lew offset + 4 * 4
                    offset += 5 * 4 //
                    val attrName = this xmlString attrNameSi
                    val attrValue = if (attrValueSi != -1) this xmlString attrValueSi
                    else "0x" + Integer.toHexString(attrResId) //
                    // region real use-case: find packageName and templateVersion
                    if (name == "manifest" && attrName == "package") {
                        packageName = attrValue //
                    }
                    if (isTemplateV1(name, attrName, attrValue)) {
                        templateVersion = 1 //
                        break@loopTag //
                    }
                    if (isTemplateV2OrV3(name, attrName, attrValue)) {
                        hasMetaTemplate = true //
                        continue@loopAttrs // next Attribute
                    }
                    if (hasMetaTemplate) {
                        templateVersion = attrResId //
                        break@loopTag //
                    } // endregion
                }
            }
            END_TAG -> {
                offset += 6 * 4 //
                // val name = this xmlString nameSi
            }
            END_DOC_TAG -> {
                break@loopTag
            }
            else -> {
                break@loopTag
            }
        }
    }
    require(packageName != null && templateVersion != -1) {
        "packageName, templateVersion invalid!, $packageName $templateVersion"
    }
    return packageName to templateVersion
}

private fun isTemplateV2OrV3(tagName: String?, attrName: String?, attrValue: String?) =
    tagName == "meta-data" && attrName == "name" && attrValue == META_DATA_TEMPLATE

private fun isTemplateV1(tagName: String?, attrName: String?, attrValue: String?) =
    tagName == "category" && attrName == "name" && attrValue == CATEGORY_TEMPLATE_APK

private fun ByteArray.xmlTagOffset(): Int {
    var result = this lew 3 * 4
    while (result < size - 4) {
        if (this lew result == START_TAG) return result
        result += 4
    }
    return result
}

private infix fun ByteArray.xmlString(stringIndex: Int): String? = if (stringIndex < 0) null else {
    val strOff = 0x24 + (this lew 4 * 4) * 4 + (this lew 0x24 + stringIndex * 4)
    val len = this[strOff + 1].toInt() shl 8 and 0xff00 or this[strOff].toInt() and 0xff
    val chars = ByteArray(len)
    for (i in 0 until len) chars[i] = this[strOff + 2 + i * 2]
    String(chars)
}

private infix fun ByteArray.lew(off: Int): Int = this[off + 3].toInt() shl 24 and -0x1000000 or
    (this[off + 2].toInt() shl 16 and 0xff0000) or (this[off + 1].toInt() shl 8 and 0xff00) or
    (this[off].toInt() and 0xFF)
