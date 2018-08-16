package template

import android.support.annotation.CheckResult

interface TemplateFactoryManager {
    @CheckResult @Throws(Exception::class)
    fun default(): Template.Default

    @CheckResult @Throws(Exception::class)
    fun version1(packageName: String, installedDate: Long): Template.Version1

    @CheckResult @Throws(Exception::class)
    fun version2(packageName: String, installedDate: Long): Template.Version2

    @CheckResult @Throws(Exception::class)
    fun version3(packageName: String, installedDate: Long): Template.Version3

    @CheckResult @Throws(Exception::class)
    fun versionHtz(htzPath: String, installedDate: Long): Template.VersionHtz
}