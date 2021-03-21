package org.illegaller.ratabb.hishoot2i.data.resolver

import common.FileConstants
import common.ext.isDirAndCanRead
import common.ext.listFilesOrEmpty
import java.io.File
import javax.inject.Inject

class HtzResolverImpl @Inject constructor(
    private val fileConstants: FileConstants
) : HtzResolver {
    override fun installedHtz(): List<File> = fileConstants.htzDir()
        .listFilesOrEmpty(File::isDirAndCanRead).toList()
}
