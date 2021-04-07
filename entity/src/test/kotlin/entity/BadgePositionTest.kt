package entity

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertThrows
import org.junit.Test

class BadgePositionTest {
    private val testObj = BadgePosition.CENTER_BOTTOM
    private val total = Sizes(5, 10)
    private val source = Sizes(4, 5)
    private val padding = 2

    @Test
    fun getValue() {
        assertThat(testObj.getValue(total, source, padding), `is`(instanceOf(SizesF::class.java)))
    }

    @Test
    fun getValueThrow() {
        val expectedThrowable = IllegalArgumentException::class.java
        val minus = Sizes(-2, -4)
        val zero = Sizes.ZERO
        assertThrows(expectedThrowable) {
            testObj.getValue(zero /* */, source, padding)
        }
        assertThrows(expectedThrowable) {
            testObj.getValue(minus /* */, source, padding)
        }
        assertThrows(expectedThrowable) {
            testObj.getValue(total, zero /* */, padding)
        }
        assertThrows(expectedThrowable) {
            testObj.getValue(total, minus /* */, padding)
        }
        assertThrows(expectedThrowable) {
            testObj.getValue(total, source, -1 /* */)
        }
    }
}
