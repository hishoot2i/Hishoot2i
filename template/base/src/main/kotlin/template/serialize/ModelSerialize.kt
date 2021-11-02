package template.serialize

import template.model.ModelHtz
import template.model.ModelV1
import template.model.ModelV2
import template.model.ModelV3
import java.io.InputStream

interface ModelSerialize {

    fun encodeModelV1(model: ModelV1): String
    fun decodeModelV1(input: InputStream): ModelV1

    fun encodeModelV2(model: ModelV2): String
    fun decodeModelV2(input: InputStream): ModelV2

    fun encodeModelV3(model: ModelV3): String
    fun decodeModelV3(input: InputStream): ModelV3

    fun encodeModelHtz(model: ModelHtz): String
    fun decodeModelHtz(input: InputStream): ModelHtz
}
