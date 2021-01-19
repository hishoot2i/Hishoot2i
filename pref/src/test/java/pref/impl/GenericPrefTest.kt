package pref.impl

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Test
import pref.BasePrefTest
import pref.SimplePref
import pref.ext.booleanPref
import pref.ext.floatPref
import pref.ext.intPref
import pref.ext.longPref
import pref.ext.stringPref
import pref.ext.stringSetPref

internal class GenericPrefTest : BasePrefTest<GenericPrefTest.TestGenericPref>() {
    override fun setupTestPref(context: Context) = TestGenericPref(context)

    companion object {
        internal const val DEFAULT_VALUE_BOOLEAN = true
        internal const val DEFAULT_VALUE_FLOAT = 1.0F
        internal const val DEFAULT_VALUE_INT = 1
        internal const val DEFAULT_VALUE_LONG = 1L
        internal const val DEFAULT_VALUE_STRING = "Ok"
        internal val DEFAULT_VALUE_STRING_SET = setOf("Ok", "Set")
    }

    internal class TestGenericPref(context: Context) : SimplePref(context, "TestGenericPref") {
        var defaultBoolean by booleanPref(default = DEFAULT_VALUE_BOOLEAN)
        var defaultFloat by floatPref(default = DEFAULT_VALUE_FLOAT)
        var defaultInt by intPref(default = DEFAULT_VALUE_INT)
        var defaultLong by longPref(default = DEFAULT_VALUE_LONG)
        var defaultString by stringPref(default = DEFAULT_VALUE_STRING)
        var defaultStringSet by stringSetPref(default = DEFAULT_VALUE_STRING_SET)
    }

    @Test
    fun `Test booleanPref default value and set new value`() {
        assertThat(testPref.defaultBoolean, `is`(DEFAULT_VALUE_BOOLEAN))
        testPref.defaultBoolean = DEFAULT_VALUE_BOOLEAN.not()
        assertThat(testPref.defaultBoolean, `is`(not(DEFAULT_VALUE_BOOLEAN)))
    }

    @Test
    fun `Test floatPref default value and set new value`() {
        assertThat(testPref.defaultFloat, `is`(DEFAULT_VALUE_FLOAT))
        testPref.defaultFloat += DEFAULT_VALUE_FLOAT
        assertThat(testPref.defaultFloat, `is`(not(DEFAULT_VALUE_FLOAT)))
    }

    @Test
    fun `Test intPref default value and set new value`() {
        assertThat(testPref.defaultInt, `is`(DEFAULT_VALUE_INT))
        testPref.defaultInt += DEFAULT_VALUE_INT
        assertThat(testPref.defaultInt, `is`(not(DEFAULT_VALUE_INT)))
    }

    @Test
    fun `Test longPref default value and set new value`() {
        assertThat(testPref.defaultLong, `is`(DEFAULT_VALUE_LONG))
        testPref.defaultLong += DEFAULT_VALUE_LONG
        assertThat(testPref.defaultLong, `is`(not(DEFAULT_VALUE_LONG)))
    }

    @Test
    fun `Test stringPref default value and set new value`() {
        assertThat(testPref.defaultString, `is`(DEFAULT_VALUE_STRING))
        testPref.defaultString = "New Value"
        assertThat(testPref.defaultString, `is`(not(DEFAULT_VALUE_STRING)))
    }

    @Test
    fun `Test stringSetPref default value and set new value`() {
        assertThat(testPref.defaultStringSet, `is`(DEFAULT_VALUE_STRING_SET))
        testPref.defaultStringSet += "New Item"
        assertThat(testPref.defaultStringSet, `is`(not(DEFAULT_VALUE_STRING_SET)))
    }
}
