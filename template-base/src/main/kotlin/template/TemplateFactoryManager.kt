package template

import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import java.io.File

interface TemplateFactoryManager {
    fun default(): Default
    fun version1(packageName: String, installedDate: Long): Version1
    fun version2(packageName: String, installedDate: Long): Version2
    fun version3(packageName: String, installedDate: Long): Version3
    fun versionHtz(path: String, installedDate: Long): VersionHtz
    fun importHtz(file: File): VersionHtz
    fun convertHtz(template: Template): VersionHtz
    fun exportHtz(versionHtz: VersionHtz): File
    fun removeHtz(versionHtz: VersionHtz): String
}
