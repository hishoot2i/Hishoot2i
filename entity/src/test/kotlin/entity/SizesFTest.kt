package entity

import entity.SizesF.Companion.toSizesF
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.comparesEqualTo
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.lessThan
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class SizesFTest : JsonBaseTest() {

    @Test
    fun createFromPair() {
        assertThat((5F to 6F).toSizesF(), `is`(equalTo(SizesF(5F, 6F))))
    }

    @Test
    fun toSize() {
        assertThat(SizesF(5F).toSize(), `is`(instanceOf(Sizes::class.java)))
    }

    @Test
    fun plusFloat() {
        assertThat(SizesF(5F) + 5F, `is`(SizesF(10F)))
    }

    @Test
    fun plusSizeF() {
        assertThat(SizesF(5F) + SizesF(2F, 3F), `is`(SizesF(7F, 8F)))
    }

    @Test
    fun minus() {
        assertThat(SizesF(5F) - SizesF(2F, 3F), `is`(SizesF(3F, 2F)))
    }

    @Test
    fun divFloat() {
        assertThat(SizesF(6F) / 2F, `is`(SizesF(3F)))
    }

    @Test
    fun divSizeF() {
        assertThat(SizesF(12F) / SizesF(2F, 3F), `is`(SizesF(6F, 4F)))
    }

    @Test
    fun timesFloat() {
        assertThat(SizesF(5F) * 3F, `is`(SizesF(15F)))
    }

    @Test
    fun compareTo() {
        assertThat(SizesF(5F, 10F), `is`(greaterThan(SizesF(4F, 9F))))
        assertThat(SizesF(4F, 10F), `is`(lessThan(SizesF(5F, 9F))))
        assertThat(SizesF(6F), `is`(comparesEqualTo(SizesF(6F, 6F))))
    }

    @Test
    fun serialize() {
        val sizesF = SizesF(5F)
        assertThat(sizesF, `is`(equalTo(default.decodeFromString<SizesF>("""{"x":5,"y":5}"""))))
        assertThat(default.decodeFromString<SizesF>("""{"x":5,"y":5}"""), `is`(equalTo(sizesF)))
        //
        assertThat(default.encodeToString(sizesF), `is`(equalTo("""{"x":5.0,"y":5.0}""")))
    }
}
