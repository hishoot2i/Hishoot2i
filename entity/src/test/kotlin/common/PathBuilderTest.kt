package common

import common.PathBuilder.DRAWABLES
import common.PathBuilder.FILES
import common.PathBuilder.TEMPLATE_APP
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PathBuilderTest {

    @Test
    fun stringTemplateApp() {
        assertThat(
            PathBuilder.stringTemplateApp("templateID", "templateAsset"),
            allOf(startsWith(TEMPLATE_APP), containsString("templateID"), endsWith("templateAsset"))
        )
    }

    @Test
    fun stringDrawables() {
        assertThat(PathBuilder.stringDrawables(123), allOf(startsWith(DRAWABLES), endsWith("123")))
    }

    @get:Rule
    var testFolder = TemporaryFolder()

    @Test
    fun stringFiles() {
        val file = testFolder.newFile()
        assertThat(PathBuilder.stringFiles(file), allOf(startsWith(FILES), endsWith(file.name)))
    }
}
