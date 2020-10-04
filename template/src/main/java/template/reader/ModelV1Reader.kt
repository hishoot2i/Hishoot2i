package template.reader

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.TEXT
import org.xmlpull.v1.XmlPullParserFactory
import template.model.ModelV1
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.LazyThreadSafetyMode.NONE

/** @see [template.model.ModelV1] */
class ModelV1Reader(
    inputStream: InputStream
) : InputStreamReader(inputStream, Charsets.UTF_8) {
    private val xmlPullParser: XmlPullParser by lazy{
        XmlPullParserFactory.newInstance().newPullParser().also { it.setInput(this) }
    }

    @Throws(Exception::class)
    fun model(): ModelV1 {
        val xpp = xmlPullParser
        val ret = ModelV1()
        var eventType = xpp.eventType
        var value: String? = null
        while (eventType != END_DOCUMENT) {
            val xppName = xpp.name
            when (eventType) {
                TEXT -> value = xpp.text
                END_TAG -> {
                    value?.let {
                        when (xppName) {
                            "device" -> ret.device = it
                            "author" -> ret.author = it
                            "topx" -> ret.topx = it.toInt()
                            "topy" -> ret.topy = it.toInt()
                            "botx" -> ret.botx = it.toInt()
                            "boty" -> ret.boty = it.toInt()
                            else -> { // ignore other xppName
                            }
                        }
                    }
                }
                else -> { // ignore other eventType
                }
            }
            eventType = xpp.nextToken()
        }
        return ret.takeUnless { it.isNotValid() }
            ?: throw IllegalStateException("NotValid Model $ret")
    }
}
