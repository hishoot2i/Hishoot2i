package pref.impl

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import pref.BasePrefTest
import pref.SimplePref
import pref.ext.enumOrdinalPref
import pref.ext.enumValuePref

internal class EnumPrefTest : BasePrefTest<EnumPrefTest.TestEnumPref>() {
    override fun setupTestPref(context: Context) = TestEnumPref(context)
    internal enum class TestEnum { RED, GREEN, BLUE }
    internal enum class TestEnumEmpty
    internal inner class TestEnumPref(context: Context) : SimplePref(context, "TestEnumPref") {
        var defaultEnumValue by enumValuePref(default = TestEnum.BLUE)
        var defaultEnumOrdinal by enumOrdinalPref(default = TestEnum.BLUE)
        var nullableEnumValue: TestEnum? by enumValuePref()
        var nullableEnumOrdinal: TestEnum? by enumOrdinalPref()
        var nullableEnumEmpty: TestEnumEmpty? by enumValuePref()
    }

    @Test
    fun `Test default enumPref instanceOf`() {
        assertThat(testPref.defaultEnumValue, `is`(instanceOf(TestEnum::class.java)))
        assertThat(testPref.defaultEnumOrdinal, `is`(instanceOf(TestEnum::class.java)))
    }

    @Test
    fun `Test empty enumPref`() {
        assertThat(testPref.nullableEnumEmpty, `is`(nullValue()))
    }

    @Test
    fun `Test default enumValuePref and set new value`() {
        assertThat(testPref.defaultEnumValue, `is`(TestEnum.BLUE))
        testPref.defaultEnumValue = TestEnum.RED
        assertThat(testPref.defaultEnumValue, `is`(TestEnum.RED))
    }

    @Test
    fun `Test default enumOrdinalPref and set new value`() {
        assertThat(testPref.defaultEnumOrdinal, `is`(TestEnum.BLUE))
        testPref.defaultEnumOrdinal = TestEnum.RED
        assertThat(testPref.defaultEnumOrdinal, `is`(TestEnum.RED))
    }

    @Test
    fun `Test nullable enumValuePref and set new value`() {
        assertThat(testPref.nullableEnumValue, `is`(nullValue()))
        testPref.nullableEnumValue = TestEnum.RED
        assertThat(testPref.nullableEnumValue, `is`(TestEnum.RED))
        assertThat(testPref.nullableEnumValue, `is`(instanceOf(TestEnum::class.java)))
        testPref.nullableEnumValue = null
        assertThat(testPref.nullableEnumValue, `is`(nullValue()))
    }

    @Test
    fun `Test nullable enumOrdinalPref and set new value`() {
        assertThat(testPref.nullableEnumOrdinal, `is`(nullValue()))
        testPref.nullableEnumOrdinal = TestEnum.GREEN
        assertThat(testPref.nullableEnumOrdinal, `is`(TestEnum.GREEN))
        assertThat(testPref.nullableEnumOrdinal, `is`(instanceOf(TestEnum::class.java)))
        testPref.nullableEnumOrdinal = null
        assertThat(testPref.nullableEnumOrdinal, `is`(nullValue()))
    }
}
