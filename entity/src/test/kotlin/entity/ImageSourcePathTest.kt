package entity

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.Test

class ImageSourcePathTest {

    private val testObj = ImageSourcePath()

    @Test
    fun defaultNullable() {
        with(testObj) {
            assertThat(background, `is`(nullValue()))
            assertThat(screen1, `is`(nullValue()))
            assertThat(screen2, `is`(nullValue()))
        }
    }

    @Test
    fun changeBackground() {
        val newValue = "new background"
        testObj.background = newValue
        assertThat(testObj.background, `is`(equalTo(newValue)))
    }

    @Test
    fun changeScreen1() {
        val newValue = "new screen1"
        testObj.screen1 = newValue
        assertThat(testObj.screen1, `is`(equalTo(newValue)))
    }

    @Test
    fun changeScreen2() {
        val newValue = "new screen2"
        testObj.screen2 = newValue
        assertThat(testObj.screen2, `is`(equalTo(newValue)))
    }
}
