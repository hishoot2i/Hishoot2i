package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer

class EndTagChunk : TagChunk() {
    override fun toString(): String {
        val form2 = "%-16s %s\n"
        val form3 = "%-16s %-16s %s\n"
        return String.format(form2, "chunkType", PrintUtil.hex4(chunkType)) + String.format(
            form2,
            "chunkSize",
            PrintUtil.hex4(chunkSize)
        ) + String.format(form2, "lineNumber", PrintUtil.hex4(lineNumber)) + String.format(
            form2,
            "unknown",
            PrintUtil.hex4(unknown)
        ) + String.format(
            form3,
            "nameSpaceUri",
            PrintUtil.hex4(nameSpaceUri),
            nameSpaceUriStr
        ) + String.format(form3, "name", PrintUtil.hex4(name), nameStr)
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer, stringChunk: StringChunk): EndTagChunk {
            val chunk = EndTagChunk()
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.lineNumber = s.readUInt()
            chunk.unknown = s.readUInt()
            chunk.nameSpaceUri = s.readUInt()
            chunk.name = s.readUInt()

            // Fill data
            chunk.nameSpaceUriStr = stringChunk.getString(chunk.nameSpaceUri.toInt())
            chunk.nameStr = stringChunk.getString(chunk.name.toInt())
            return chunk
        }
    }
}
