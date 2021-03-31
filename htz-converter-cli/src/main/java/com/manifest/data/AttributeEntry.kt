package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer

class AttributeEntry {
    var namespaceUri: Long = 0
    var name: Long = 0
    var valueString: Long = 0

    @JvmField
    var type: Long = 0

    @JvmField
    var data: Long = 0

    // Assistant
    @JvmField
    var namespaceUriStr: String? = null

    @JvmField
    var nameStr: String? = null
    var valueStringStr: String? = null
    var typeStr: String? = null

    @JvmField
    var dataStr: String? = null

    override fun toString(): String = "%-16s %-16s %s\n".run {
        format("namespaceUri", PrintUtil.hex4(namespaceUri), namespaceUriStr) +
            format("valueString", PrintUtil.hex4(valueString), valueStringStr) +
            format("type", PrintUtil.hex4(type), typeStr) +
            format("data", PrintUtil.hex4(data), dataStr)
    }

    companion object {

        @JvmStatic
        fun parseFrom(s: MfStreamer, stringChunk: StringChunk): AttributeEntry {
            val entry = AttributeEntry()
            entry.namespaceUri = s.readUInt()
            entry.name = s.readUInt()
            entry.valueString = s.readUInt()
            entry.type = s.readUInt() shr 24
            entry.data = s.readUInt()
            // Fill data
            entry.namespaceUriStr = stringChunk.getString(entry.namespaceUri.toInt())
            entry.nameStr = stringChunk.getString(entry.name.toInt())
            entry.valueStringStr = stringChunk.getString(entry.valueString.toInt())
            entry.typeStr = AttributeType.getAttributeType(entry)
            entry.dataStr = AttributeType.getAttributeData(entry, stringChunk)
            return entry
        }
    }
}
