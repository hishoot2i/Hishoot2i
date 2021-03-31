package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer
import java.util.Locale

class ResourceIdChunk {
    var chunkType: Long = 0
    var chunkSize: Long = 0
    var resourceIds: LongArray = longArrayOf()

    // Assistant
    var numIds = 0
    override fun toString(): String {
        val builder = StringBuilder(512)
        val formH = "%-16s %s\n"
        builder.append("-- ResourceId Chunk --").append('\n')
        builder.append(formH.format("chunkType", PrintUtil.hex4(chunkType)))
        builder.append(formH.format("chunkSize", PrintUtil.hex4(chunkSize)))
        builder.append("|----|------------|----------").append('\n')
        builder.append("| Id |     hex    |    dec   ").append('\n')
        builder.append("|----|------------|----------").append('\n')
        val formC = "|%-4d| 0x%-8s | %8d\n"
        for (i in 0 until numIds) {
            builder.append(
                formC.format(Locale.ROOT, i, PrintUtil.hex4(resourceIds[i]), resourceIds[i])
            )
        }
        return builder.toString()
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer): ResourceIdChunk {
            val chunk = ResourceIdChunk()
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.numIds = ((chunk.chunkSize - 8) / 4).toInt()
            chunk.resourceIds = LongArray(chunk.numIds)
            for (i in 0 until chunk.numIds) {
                chunk.resourceIds[i] = s.readUInt()
            }
            return chunk
        }
    }
}
