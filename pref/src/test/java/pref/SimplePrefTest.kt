package pref

import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SimplePrefTest {

    private lateinit var simplePref: SimplePref

    @Before
    fun setup() {
        simplePref = object : SimplePref(
            InstrumentationRegistry.getInstrumentation().targetContext
        ) {
            // Eew!
        }
    }

    @Test
    fun `Test preferences not null`() {
        assertThat(simplePref.preferences, `is`(notNullValue()))
    }

    @Test
    fun `Test preferences instance`() {
        assertThat(simplePref.preferences, `is`(instanceOf(SharedPreferences::class.java)))
    }
}
