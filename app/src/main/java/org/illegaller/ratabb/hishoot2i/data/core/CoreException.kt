package org.illegaller.ratabb.hishoot2i.data.core

@Suppress("unused")
class CoreException : RuntimeException {
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
}