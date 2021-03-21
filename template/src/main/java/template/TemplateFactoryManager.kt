package template

import androidx.annotation.WorkerThread
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import java.io.File

@WorkerThread
interface TemplateFactoryManager {
    fun default(): Default
    fun version1(name: String, installed: Long): Version1
    fun version2(name: String, installed: Long): Version2
    fun version3(name: String, installed: Long): Version3
    fun versionHtz(path: String, installed: Long): VersionHtz
    fun importHtz(file: File): VersionHtz
    fun convertHtz(template: Template): VersionHtz
    fun exportHtz(versionHtz: VersionHtz): File
    fun removeHtz(versionHtz: VersionHtz): String
}
