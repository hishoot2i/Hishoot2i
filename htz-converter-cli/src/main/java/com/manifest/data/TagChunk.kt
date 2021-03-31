package com.manifest.data

open class TagChunk {
    var chunkType: Long = 0
    var chunkSize: Long = 0
    var lineNumber: Long = 0
    var unknown: Long = 0
    var nameSpaceUri: Long = 0
    var name: Long = 0 // Tag name index

    // Assistant
    var nameSpaceUriStr: String? = null
    @JvmField
    var nameStr: String? = null
}
