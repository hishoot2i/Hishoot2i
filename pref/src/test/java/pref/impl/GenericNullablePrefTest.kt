package pref.impl

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import pref.BasePrefTest
import pref.SimplePref
import pref.ext.booleanPref
import pref.ext.floatPref
import pref.ext.intPref
import pref.ext.longPref
import pref.ext.stringPref
import pref.ext.stringSetPref

internal class GenericNullablePrefTest : BasePrefTest<GenericNullablePrefTest.TestNullablePref>() {
    override fun setupTestPref(context: Context) = TestNullablePref(context)

    internal class TestNullablePref(context: Context) : SimplePref(context, "TestNullablePref") {
        var nullableBoolean by booleanPref()
        var nullableFloat by floatPref()
        var nullableInt by intPref()
        var nullableLong by longPref()
        var nullableString by stringPref()
        var nullableStringSet by stringSetPref()
    }

    @Test
    fun `Test nullable booleanPref value and set new value`() {
        assertThat(testPref.nullableBoolean, `is`(nullValue()))
        testPref.nullableBoolean = true
        assertThat(testPref.nullableBoolean, `is`(true))
        testPref.nullableBoolean = null
        assertThat(testPref.nullableBoolean, `is`(nullValue()))
    }

    @Test
    fun `Test nullable stringSetPref value and set new value`() {
        assertThat(testPref.nullableStringSet, `is`(nullValue()))
        val newValue = setOf("set", "test")
        testPref.nullableStringSet = newValue
        assertThat(testPref.nullableStringSet, `is`(newValue))
        testPref.nullableStringSet = null
        assertThat(testPref.nullableStringSet, `is`(nullValue()))
    }

    @Test
    fun `Test nullable stringPref value and set new value`() {
        assertThat(testPref.nullableString, `is`(nullValue()))
        val newValue = "test"
        testPref.nullableString = newValue
        assertThat(testPref.nullableString, `is`(newValue))
        testPref.nullableString = null
        assertThat(testPref.nullableString, `is`(nullValue()))
    }

    @Test
    fun `Test nullable longPref value and set new value`() {
        assertThat(testPref.nullableLong, `is`(nullValue()))
        val newValue = 30L
        testPref.nullableLong = newValue
        assertThat(testPref.nullableLong, `is`(newValue))
        testPref.nullableLong = null
        assertThat(testPref.nullableLong, `is`(nullValue()))
    }

    @Test
    fun `Test nullable intPref value and set new value`() {
        assertThat(testPref.nullableInt, `is`(nullValue()))
        val newValue = 123
        testPref.nullableInt = newValue
        assertThat(testPref.nullableInt, `is`(newValue))
        testPref.nullableInt = null
        assertThat(testPref.nullableInt, `is`(nullValue()))
    }

    @Test
    fun `Test nullable floatPref value and set new value`() {
        assertThat(testPref.nullableFloat, `is`(nullValue()))
        val newValue = 0.5F
        testPref.nullableFloat = newValue
        assertThat(testPref.nullableFloat, `is`(newValue))
        testPref.nullableFloat = null
        assertThat(testPref.nullableFloat, `is`(nullValue()))
    }
}
