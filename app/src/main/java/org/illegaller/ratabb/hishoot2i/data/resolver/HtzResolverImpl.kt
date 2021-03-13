package org.illegaller.ratabb.hishoot2i.data.resolver

import common.FileConstants
import common.ext.isDirAndCanRead
import common.ext.listFilesOrEmpty
import java.io.File
import javax.inject.Inject

class HtzResolverImpl @Inject constructor(fileConstants: FileConstants) : HtzResolver {
    private val htzDir: () -> File = (fileConstants::htzDir)
    override fun installedHtz(): List<File> =
        htzDir().listFilesOrEmpty().filter(File::isDirAndCanRead)
}
