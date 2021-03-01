package org.illegaller.ratabb.hishoot2i.data.resolver

import java.io.File

interface HtzResolver {
    suspend fun installedHtz(): List<File>
}
