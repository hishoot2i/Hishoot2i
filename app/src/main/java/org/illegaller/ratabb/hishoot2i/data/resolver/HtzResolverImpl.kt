package org.illegaller.ratabb.hishoot2i.data.resolver

import common.FileConstants
import common.ext.isDirAndCanRead
import common.ext.listFilesOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class HtzResolverImpl @Inject constructor(fileConstants: FileConstants) : HtzResolver {
    private val htzDir: () -> File = (fileConstants::htzDir)
    override suspend fun installedHtz(): List<File> =
        withContext(Dispatchers.IO) { htzDir().listFilesOrEmpty().filter { it.isDirAndCanRead() } }
}
