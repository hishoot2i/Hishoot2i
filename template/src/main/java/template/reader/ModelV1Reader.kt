@file:Suppress("SpellCheckingInspection")

package template.reader

import androidx.annotation.WorkerThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.TEXT
import org.xmlpull.v1.XmlPullParserFactory
import template.model.ModelV1
import java.io.InputStream

/** @see [ModelV1] */
@WorkerThread
class ModelV1Reader(inputStream: InputStream) : BaseModelReader<ModelV1>(inputStream) {
    private val xmlPullParser: XmlPullParser by lazy {
        XmlPullParserFactory.newInstance().newPullParser().also { it.setInput(this) }
    }

    override fun model(): ModelV1 {
        val xpp = xmlPullParser
        val ret = ModelV1()
        var eventType = xpp.eventType
        var value: String? = null
        loop@ while (eventType != END_DOCUMENT) {
            val xppName = xpp.name
            when (eventType) {
                TEXT -> value = xpp.text
                END_TAG -> {
                    value = value ?: continue@loop
                    when (xppName) {
                        "device" -> ret.device = value
                        "author" -> ret.author = value
                        "topx" -> ret.topx = value.toInt()
                        "topy" -> ret.topy = value.toInt()
                        "botx" -> ret.botx = value.toInt()
                        "boty" -> ret.boty = value.toInt()
                        else -> { // ignore other xppName
                        }
                    }
                }
                else -> { // ignore other eventType
                }
            }
            eventType = xpp.nextToken()
        }
        check(!ret.isNotValid()) { "Not valid model: $ret" }
        return ret
    }
}
