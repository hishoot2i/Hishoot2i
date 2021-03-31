package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer
import java.util.ArrayList

class StartTagChunk : TagChunk() {
    var flags: Long = 0 // Flags indicating start tag or end tag.
    var attributeCount: Long = 0 // Count of attributes in tag
    var classAttribute: Long = 0
    lateinit var attributes: MutableList<AttributeEntry>
    override fun toString(): String {
        val builder = StringBuilder(512)
        val form2 = "%-16s %s\n"
        val form3 = "%-16s %-16s %s\n"
        builder.append(String.format(form2, "chunkType", PrintUtil.hex4(chunkType)))
        builder.append(String.format(form2, "chunkSize", PrintUtil.hex4(chunkSize)))
        builder.append(String.format(form2, "lineNumber", PrintUtil.hex4(lineNumber)))
        builder.append(String.format(form2, "unknown", PrintUtil.hex4(unknown)))
        builder.append(
            String.format(
                form3,
                "nameSpaceUri",
                PrintUtil.hex4(nameSpaceUri),
                nameSpaceUriStr
            )
        )
        builder.append(String.format(form3, "name", PrintUtil.hex4(name), nameStr))
        builder.append(String.format(form2, "flags", PrintUtil.hex4(flags)))
        builder.append(String.format(form2, "attributeCount", PrintUtil.hex4(attributeCount)))
        builder.append(String.format(form2, "classAttribute", PrintUtil.hex4(classAttribute)))
        for (i in 0 until attributeCount) {
            builder.append(" <AttributeEntry.").append(i).append(" />").append('\n')
            builder.append(attributes[i.toInt()])
        }
        return builder.toString()
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer, stringChunk: StringChunk): StartTagChunk {
            val chunk = StartTagChunk()
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.lineNumber = s.readUInt()
            chunk.unknown = s.readUInt()
            chunk.nameSpaceUri = s.readUInt()
            chunk.name = s.readUInt()
            chunk.flags = s.readUInt()
            chunk.attributeCount = s.readUInt()
            chunk.classAttribute = s.readUInt()
            chunk.attributes = ArrayList(chunk.attributeCount.toInt())
            var i = 0
            while (i < chunk.attributeCount) {
                chunk.attributes.add(AttributeEntry.parseFrom(s, stringChunk))
                ++i
            }

            // Fill data
            chunk.nameSpaceUriStr = stringChunk.getString(chunk.nameSpaceUri.toInt())
            chunk.nameStr = stringChunk.getString(chunk.name.toInt())
            return chunk
        }
    }
}
