package rbb.hishoot2i.template.reader

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import rbb.hishoot2i.template.TemplateException
import rbb.hishoot2i.template.model.ModelV1
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.LazyThreadSafetyMode.NONE

/** @see [ModelV1] */
class ModelV1Reader(inputStream: InputStream) : InputStreamReader(inputStream, Charsets.UTF_8) {
    private val xmlPullParser: XmlPullParser by lazy(NONE) {
        XmlPullParserFactory.newInstance()
            .newPullParser()
            .also { it.setInput(this) }
    }

    @Throws(Exception::class)
    fun model(): ModelV1 {
        val xpp = xmlPullParser
        val ret = ModelV1()
        var eventType = xpp.eventType
        var value: String? = null
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val xppName = xpp.name
            when (eventType) {
                XmlPullParser.TEXT -> value = xpp.text
                XmlPullParser.END_TAG -> {
                    value?.let {
                        when (xppName) {
                            "device" -> ret.device = it
                            "author" -> ret.author = it
                            "topx" -> ret.topx = it.toInt()
                            "topy" -> ret.topy = it.toInt()
                            "botx" -> ret.botx = it.toInt()
                            "boty" -> ret.boty = it.toInt()
                            else -> { // ignore xppName
                            }
                        }
                    }
                }
                else -> { // ignore eventType
                }
            }
            eventType = xpp.nextToken()
        }
        return when {
            ret.isNotValid() -> throw TemplateException("NotValid ModelV1")
            else -> ret
        }
    }
}