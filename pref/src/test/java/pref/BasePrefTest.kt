package pref

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pref.ext.clear

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal abstract class BasePrefTest<T : Pref> {
    internal lateinit var testPref: T
    abstract fun setupTestPref(context: Context): T

    @Before
    fun setup() {
        testPref = setupTestPref(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
    }

    @After
    fun tearDown() {
        testPref.clear()
    }
}
