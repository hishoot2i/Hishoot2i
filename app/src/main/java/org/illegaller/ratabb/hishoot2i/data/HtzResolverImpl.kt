package org.illegaller.ratabb.hishoot2i.data

import common.FileConstants
import common.ext.isDirAndCanRead
import common.ext.listFilesOrEmpty
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import java.io.File
import javax.inject.Inject

class HtzResolverImpl @Inject constructor(fileConstants: FileConstants) : HtzResolver {
    private val htzDir: () -> File = (fileConstants::htzDir)
    override fun installedHtz(): Flowable<File> = Flowable.fromCallable { htzDir() }
        .map { it.listFilesOrEmpty() }
        .flatMap { it.toFlowable() }
        .filter { it.isDirAndCanRead() }
}
