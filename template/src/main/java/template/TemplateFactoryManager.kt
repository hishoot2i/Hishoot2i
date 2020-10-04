package template

import androidx.annotation.CheckResult
import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz

interface TemplateFactoryManager {
    @CheckResult @Throws(Exception::class)
    fun default(): Default

    @CheckResult @Throws(Exception::class)
    fun version1(packageName: String, installedDate: Long): Version1

    @CheckResult @Throws(Exception::class)
    fun version2(packageName: String, installedDate: Long): Version2

    @CheckResult @Throws(Exception::class)
    fun version3(packageName: String, installedDate: Long): Version3

    @CheckResult @Throws(Exception::class)
    fun versionHtz(htzPath: String, installedDate: Long): VersionHtz
}
