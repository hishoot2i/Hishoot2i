@file:Suppress("SpellCheckingInspection")

package template.serialize

import TestHelper.resourceStreamOrThrow
import entity.Glare
import entity.Sizes
import entity.SizesF
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.Test
import template.model.ModelHtz
import template.model.ModelV1
import template.model.ModelV2
import template.model.ModelV3

class ModelSerializeImplTest {

    private val testObj = ModelSerializeImpl

    private val modelV1 by lazy {
        ModelV1(
            device = "Galaxy Gio",
            author = "http://androidminang.com",
            topx = 42,
            topy = 140,
            botx = 39,
            boty = 190
        )
    }
    private val modelV2 by lazy {
        ModelV2(
            name = "Ipin69 Miring",
            author = "Bsod",
            left_top_x = 394,
            left_top_y = 242,
            right_top_x = 960,
            right_top_y = 199,
            left_bottom_x = 807,
            left_bottom_y = 1149,
            right_bottom_x = 1443,
            right_bottom_y = 964,
            template_width = 1856,
            template_height = 1456
        )
    }
    private val modelV3 by lazy {
        val glares = listOf(
            Glare(name = "bulet_lonjong", size = Sizes(148, 36), position = SizesF(251F, 187F)),
            Glare(name = "buletan", size = Sizes(139, 59), position = SizesF(732F, y = 470F))
        )
        val coordinate = listOf(162F, 257F, 597F, 188F, 481F, 513F, 987F, 414F)
        ModelV3(
            author = "rbb",
            name = "Sample HiShoot2i Template v3",
            desc = "image from cssauthor.com",
            coordinate = coordinate,
            size = Sizes(1200, 840),
            preview = "tampilin",
            frame = "base",
            shadow = null,
            glares = glares
        )
    }
    private val modelHtz by lazy {
        ModelHtz(
            name = "Sample Htz",
            author = "fb.com/ratabb",
            template_file = "frame_sample.png",
            preview = "preview_sample.jpg",
            overlay_file = "overlay_sample.png",
            overlay_x = 148,
            overlay_y = 206,
            screen_width = 720,
            screen_height = 1280,
            screen_x = 200,
            screen_y = 300,
            template_width = 1120,
            template_height = 2080
        )
    }

    @Test
    fun encodeModelV1() {
        val actual = testObj.encodeModelV1(modelV1)
        assertThat(actual, `is`(notNullValue(String.javaClass)))
        assertThat(actual, anyOf(containsString(modelV1.author), containsString(modelV1.device)))
    }

    @Test
    fun decodeModelV1() {
        val stream = resourceStreamOrThrow("modelv1_keterangan.xml")
        val actual = testObj.decodeModelV1(stream)
        assertThat(actual, `is`(notNullValue(ModelV1.javaClass)))
        assertThat(actual, `is`(equalTo(modelV1)))
    }

    @Test
    fun encodeModelV2() {
        val actual = testObj.encodeModelV2(modelV2)
        assertThat(actual, `is`(notNullValue(String.javaClass)))
        assertThat(actual, anyOf(containsString(modelV2.author), containsString(modelV2.name)))
    }

    @Test
    fun decodeModelV2() {
        val stream = resourceStreamOrThrow("modelv2_template.cfg")
        val actual = testObj.decodeModelV2(stream)
        assertThat(actual, `is`(notNullValue(ModelV2.javaClass)))
        assertThat(actual, `is`(equalTo(modelV2)))
    }

    @Test
    fun encodeModelV3() {
        val actual = testObj.encodeModelV3(modelV3)
        assertThat(actual, `is`(notNullValue(String.javaClass)))
        assertThat(actual, anyOf(containsString(modelV3.author), containsString(modelV3.name)))
    }

    @Test
    fun decodeModelV3() {
        val stream = resourceStreamOrThrow("modelv3_template.cfg")
        val actual = testObj.decodeModelV3(stream)
        assertThat(actual, `is`(notNullValue(ModelV3.javaClass)))
        assertThat(actual, `is`(equalTo(modelV3)))
    }

    @Test
    fun encodeModelHtz() {
        val actual = testObj.encodeModelHtz(modelHtz)
        assertThat(actual, `is`(notNullValue(String.javaClass)))
        assertThat(actual, anyOf(containsString(modelHtz.author), containsString(modelHtz.name)))
    }

    @Test
    fun decodeModelHtz() {
        val stream = resourceStreamOrThrow("modelHtz_template.cfg")
        val actual = testObj.decodeModelHtz(stream)
        assertThat(actual, `is`(notNullValue(ModelHtz.javaClass)))
        assertThat(actual, `is`(equalTo(modelHtz)))
    }
}
