@file:Suppress("SpellCheckingInspection")

package command

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.any
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ConvertCommandTest {

    @get:Rule
    var testFolder = TemporaryFolder()

    private val validApkFilePath by lazy {
        File("src/test/resources", "test.apk.template").absolutePath
    }
    private val validDirPath by lazy { testFolder.root.absolutePath }

    @Test
    fun invalidInputThrowException() {
        assertThrows(IllegalArgumentException::class.java) {
            ConvertCommand(
                apkFilePath = "invalidFileApk",
                tempDir = validDirPath,
                outputDir = validDirPath
            ).run()
        }
        assertThrows(IllegalArgumentException::class.java) {
            ConvertCommand(
                apkFilePath = validApkFilePath,
                tempDir = "invalidTemp",
                outputDir = validDirPath
            ).run()
        }
        assertThrows(IllegalArgumentException::class.java) {
            ConvertCommand(
                apkFilePath = validApkFilePath,
                tempDir = validDirPath,
                outputDir = "invalidOutput"
            ).run()
        }
    }

    @Test
    fun validInput() {
        val convertCommand = ConvertCommand(
            apkFilePath = validApkFilePath,
            tempDir = validDirPath,
            outputDir = validDirPath
        )
        assertThat(convertCommand.run(), `is`(any(Unit::class.java)))
    }
}
