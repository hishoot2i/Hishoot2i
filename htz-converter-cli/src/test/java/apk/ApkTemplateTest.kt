@file:Suppress("SpellCheckingInspection")

package apk

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.any
import org.junit.Assert.assertThrows
import org.junit.Test
import template.entryInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

class ApkTemplateTest {

    private fun androidManifestFrom(name: String): InputStream =
        File("src/test/resources", "$name$ANDROID_MANIFEST").inputStream()

    @Test
    fun parseTemplateApk() {
        val androidManifestFromApk = ZipFile(File("src/test/resources", "test.apk.template"))
            .entryInputStream(ANDROID_MANIFEST)
        val (packageName, templateVersion) = androidManifestFromApk.binaryXmlManifest()
        assertThat(packageName, `is`(any(String::class.java)))
        assertThat(templateVersion, `is`(any(Int::class.java)))
    }

    @Test
    fun parseTemplateV1() {
        val (packageName, templateVersion) = androidManifestFrom("templateV1_").binaryXmlManifest()
        assertThat(packageName, `is`("org.illegals.template_hishoot.dvdplasticcase"))
        assertThat(templateVersion, `is`(1))
    }

    @Test
    fun parseTemplateV2() {
        val (packageName, templateVersion) = androidManifestFrom("templateV2_").binaryXmlManifest()
        assertThat(packageName, `is`("tempe.bsod.menjesse"))
        assertThat(templateVersion, `is`(2))
    }

    @Test
    fun parseTemplateV3() {
        val (packageName, templateVersion) = androidManifestFrom("templateV3_").binaryXmlManifest()
        assertThat(packageName, `is`("id.ratabb.templatev3.sample"))
        assertThat(templateVersion, `is`(3))
    }

    @Test
    fun parseInvalidTemplate() {
        assertThrows(IllegalArgumentException::class.java) {
            androidManifestFrom("invalid_").binaryXmlManifest()
        }
    }
}
