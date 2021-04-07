package entity

import entity.Sizes.Companion.toSizes
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.comparesEqualTo
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.lessThan
import org.junit.Test

class SizesTest {

    @Test
    fun createFromPair() {
        assertThat((5 to 6).toSizes(), `is`(equalTo(Sizes(5, 6))))
    }

    @Test
    fun toSizeF() {
        assertThat(Sizes(5).toSizeF(), `is`(instanceOf(SizesF::class.java)))
    }

    @Test
    fun plusInt() {
        assertThat(Sizes(5) + 5, `is`(Sizes(10)))
    }

    @Test
    fun plusSize() {
        assertThat(Sizes(5) + Sizes(1, 3), `is`(Sizes(6, 8)))
    }

    @Test
    fun minusInt() {
        assertThat(Sizes(5) - 3, `is`(Sizes(2)))
    }

    @Test
    fun minusSize() {
        assertThat(Sizes(5) - Sizes(3, 2), `is`(Sizes(2, 3)))
    }

    @Test
    fun divInt() {
        assertThat(Sizes(6) / 3, `is`(Sizes(2, 2)))
    }

    @Test
    fun compareTo() {
        assertThat(Sizes(5, 10), `is`(greaterThan(Sizes(4, 9))))
        assertThat(Sizes(4, 10), `is`(lessThan(Sizes(5, 9))))
        assertThat(Sizes(6), `is`(comparesEqualTo(Sizes(6, 6))))
    }

    @Test
    fun maxInt() {
        assertThat(Sizes(5, 10).max(7), `is`(Sizes(3, 7)))
    }

    @Test
    fun maxSize() {
        assertThat(Sizes(5, 10).max(Sizes(2)), `is`(Sizes(1, 2)))
    }

    @Test
    fun shortSide() {
        assertThat(Sizes(5, 10).shortSide(), `is`(Sizes(5)))
    }
}
