package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer
import java.util.HashMap

class StartNamespaceChunk {
    var chunkType: Long = 0
    var chunkSize: Long = 0
    var lineNumber: Long = 0
    var unknown: Long = 0
    var prefixIdx: Long = 0
    var uriIdx: Long = 0
    var prefixStr: String? = null
    var uriStr: String? = null
    lateinit var uri2prefixMap: MutableMap<String?, String?>
    lateinit var prefix2UriMap: MutableMap<String?, String?>
    override fun toString(): String {
        val builder = StringBuilder(256)
        val form2 = "%-16s %s\n"
        val form3 = "%-16s %s   %s\n"
        builder.append("-- StartNamespace Chunk --").append('\n')
        builder.append(String.format(form2, "chunkType", PrintUtil.hex4(chunkType)))
        builder.append(String.format(form2, "chunkSize", PrintUtil.hex4(chunkSize)))
        builder.append(String.format(form2, "lineNumber", PrintUtil.hex4(lineNumber)))
        builder.append(String.format(form2, "unknown", PrintUtil.hex4(unknown)))
        builder.append(String.format(form3, "prefixIdx", PrintUtil.hex4(prefixIdx), prefixStr))
        builder.append(String.format(form3, "uriIdx", PrintUtil.hex4(uriIdx), uriStr))
        builder.append("--------------------------\n")
        for ((key, value) in prefix2UriMap) {
            builder.append("xmlns:$key=$value\n")
        }
        return builder.toString()
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer, stringChunk: StringChunk): StartNamespaceChunk {
            val chunk = StartNamespaceChunk()
            // Meta data
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.lineNumber = s.readUInt()
            chunk.unknown = s.readUInt()
            chunk.prefixIdx = s.readUInt()
            chunk.uriIdx = s.readUInt()
            // Fill data
            chunk.prefixStr = stringChunk.getString(chunk.prefixIdx.toInt())
            chunk.uriStr = stringChunk.getString(chunk.uriIdx.toInt())
            chunk.uri2prefixMap = HashMap()
            chunk.prefix2UriMap = HashMap()
            chunk.uri2prefixMap[chunk.uriStr] = chunk.prefixStr
            chunk.prefix2UriMap[chunk.prefixStr] = chunk.uriStr
            return chunk
        }
    }
}
