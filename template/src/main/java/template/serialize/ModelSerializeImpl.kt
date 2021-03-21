package template.serialize

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import template.model.ModelHtz
import template.model.ModelV1
import template.model.ModelV2
import template.model.ModelV3
import java.io.InputStream

internal object ModelSerializeImpl : ModelSerialize {

    private val xmlFormat by lazy { XML { unknownChildHandler = { _, _, _, _ -> } } }
    private val jsonFormat by lazy { Json { ignoreUnknownKeys = true; encodeDefaults = true } }

    override fun encodeModelV1(model: ModelV1): String = xmlFormat.encodeToString(model)
    override fun decodeModelV1(input: InputStream): ModelV1 =
        xmlFormat.decodeFromString(input.bufferedReadText())

    override fun encodeModelV2(model: ModelV2): String = jsonFormat.encodeToString(model)
    override fun decodeModelV2(input: InputStream): ModelV2 =
        jsonFormat.decodeFromString(input.bufferedReadText())

    override fun encodeModelV3(model: ModelV3): String = jsonFormat.encodeToString(model)
    override fun decodeModelV3(input: InputStream): ModelV3 =
        jsonFormat.decodeFromString(input.bufferedReadText())

    override fun encodeModelHtz(model: ModelHtz): String = jsonFormat.encodeToString(model)
    override fun decodeModelHtz(input: InputStream): ModelHtz =
        jsonFormat.decodeFromString(input.bufferedReadText())

    private fun InputStream.bufferedReadText(): String = bufferedReader().readText()
}
