package org.illegaller.ratabb.hishoot2i.data.resolver

import java.io.File

interface HtzResolver {
    fun installedHtz(): List<File>
}
