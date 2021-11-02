package entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class GlareTest : JsonBaseTest() {

    @Test
    fun serialize() {
        val glare = Glare("test", Sizes.ZERO, SizesF.ZERO)
        val jsonGlare = """{"name":"test","size":{"x":0,"y":0},"position":{"x":0.0,"y":0.0}}"""
        assertThat(glare, `is`(equalTo(default.decodeFromString<Glare>(jsonGlare))))
        assertThat(default.encodeToString(glare), `is`(equalTo(jsonGlare)))
    }
}
