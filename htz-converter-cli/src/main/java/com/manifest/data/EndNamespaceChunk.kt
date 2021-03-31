package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer

class EndNamespaceChunk {
    var chunkType: Long = 0
    var chunkSize: Long = 0
    var lineNumber: Long = 0
    var unknown: Long = 0
    var prefixIdx: Long = 0
    var uriIdx: Long = 0

    // Assistant
    var prefixStr: String? = null
    var uriStr: String? = null
    override fun toString(): String {
        val form2 = "%-16s %s\n"
        val form3 = "%-16s %s   %s\n"
        return """
            -- EndNamespace Chunk --
            ${String.format(form2, "chunkType", PrintUtil.hex4(chunkType))}${
        String.format(form2, "chunkSize", PrintUtil.hex4(chunkSize))
        }${String.format(form2, "lineNumber", PrintUtil.hex4(lineNumber))}${
        String.format(form2, "unknown", PrintUtil.hex4(unknown))
        }${String.format(form3, "prefixIdx", PrintUtil.hex4(prefixIdx), prefixStr)}${
        String.format(form3, "uriIdx", PrintUtil.hex4(uriIdx), uriStr)
        }
        """.trimIndent()
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer, stringChunk: StringChunk): EndNamespaceChunk {
            val chunk = EndNamespaceChunk()
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.lineNumber = s.readUInt()
            chunk.unknown = s.readUInt()
            chunk.prefixIdx = s.readUInt()
            chunk.uriIdx = s.readUInt()
            chunk.prefixStr = stringChunk.getString(chunk.prefixIdx.toInt())
            chunk.uriStr = stringChunk.getString(chunk.uriIdx.toInt())
            return chunk
        }
    }
}
