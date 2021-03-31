package com.manifest.data

import com.manifest.stream.MfStreamer
import java.util.Locale

class ChunkInfo {
    @JvmField
    var chunkType: Long = 0

    @JvmField
    var chunkSize: Long = 0

    // Assistant
    @JvmField
    var chunkIndex = 0
    override fun toString(): String = "%-2d  type=%08x  size=%-6d  %s".format(
        Locale.ROOT, chunkIndex, chunkType, chunkSize, getChunkType(chunkType)
    )

    private fun getChunkType(type: Long): String = when (type.toInt()) {
        STRING_CHUNK_ID -> "StringChunk"
        RESOURCE_ID_CHUNK_ID -> "ResourceIdChunk"
        START_NAMESPACE_CHUNK_ID -> "StartNamespaceChunk"
        START_TAG_CHUNK_ID -> "StartTagChunk"
        EDN_TAG_CHUNK_ID -> "EndTagChunk"
        CHUNK_END_NS_CHUNK_ID -> "ChunkEndsChunk"
        else -> "UnknownChunk"
    }

    companion object {
        const val LENGTH = 8
        const val STRING_CHUNK_ID = 0x001C0001
        const val RESOURCE_ID_CHUNK_ID = 0x00080180
        const val START_NAMESPACE_CHUNK_ID = 0x00100100
        const val START_TAG_CHUNK_ID = 0x00100102
        const val EDN_TAG_CHUNK_ID = 0x00100103
        const val CHUNK_END_NS_CHUNK_ID = 0x00100101

        @JvmStatic
        fun parseFrom(s: MfStreamer): ChunkInfo {
            val chunk = ChunkInfo()
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            return chunk
        }
    }
}
