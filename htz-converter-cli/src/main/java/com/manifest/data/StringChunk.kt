package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer

class StringChunk {
    var chunkType: Long = 0
    var chunkSize: Long = 0
    var stringCount: Long = 0
    var styleCount: Long = 0
    var unknown: Long = 0
    var stringPoolOffset: Long = 0
    var stylePoolOffset: Long = 0

    var stringOffset: LongArray = longArrayOf() // Offset of each string
    var styleOffset: LongArray = longArrayOf() // Offset of each string
    var stringLens: IntArray = intArrayOf() // Length of each string
    var styleLens: IntArray = intArrayOf() // Length of each style
    var strings: Array<String?> = emptyArray() // Content of each string
    var styles: Array<String?> = emptyArray() // Content of each style

    fun getString(index: Int): String? =
        if (index >= 0 && index < strings.size) strings[index] else null

    fun getString(index: Long): String? =
        if (index >= 0 && index < strings.size) strings[index.toInt()] else null

    @Suppress("unused")
    fun getStyle(index: Int): String? =
        if (index >= 0 && index < styles.size) styles[index] else null

    override fun toString(): String {
        val builder = StringBuilder(1024)
        val formH = "%-16s %s\n"
        builder.append("-- String Chunk --").append('\n')
        builder.append(formH.format("chunkType", PrintUtil.hex4(chunkType)))
        builder.append(formH.format("chunkSize", PrintUtil.hex4(chunkSize)))
        builder.append(formH.format("stringCount", PrintUtil.hex4(stringCount)))
        builder.append(formH.format("styleCount", PrintUtil.hex4(styleCount)))
        builder.append(formH.format("unknown", PrintUtil.hex4(unknown)))
        builder.append(formH.format("stringPoolOffset", PrintUtil.hex4(stringPoolOffset)))
        builder.append(formH.format("stylePoolOffset", PrintUtil.hex4(stylePoolOffset)))
        builder.append("|----|----------|-----|---------").append('\n')
        builder.append("| Id |  Offset  | Len | Content").append('\n')
        builder.append("|----|----------|-----|---------").append('\n')
        val formC = "|%-4d| %-8s | %-3d | %s\n"
        for (i in 0 until stringCount) {
            builder.append(
                formC.format(
                    i,
                    PrintUtil.hex4(stringOffset[i.toInt()]),
                    stringLens[i.toInt()], strings[i.toInt()]
                )
            )
        }
        for (i in 0 until styleCount) {
            builder.append(
                formC.format(
                    i,
                    PrintUtil.hex4(styleOffset[i.toInt()]),
                    styleLens[i.toInt()], styles[i.toInt()]
                )
            )
        }
        return builder.toString()
    }

    companion object {
        @JvmStatic
        fun parseFrom(s: MfStreamer): StringChunk {
            val chunk = StringChunk()
            // Chunk header
            chunk.chunkType = s.readUInt()
            chunk.chunkSize = s.readUInt()
            chunk.stringCount = s.readUInt()
            chunk.styleCount = s.readUInt()
            chunk.unknown = s.readUInt()
            chunk.stringPoolOffset = s.readUInt()
            chunk.stylePoolOffset = s.readUInt()
            chunk.stringOffset = LongArray(chunk.stringCount.toInt())
            chunk.styleOffset = LongArray(chunk.styleCount.toInt())
            for (i in chunk.stringOffset.indices) chunk.stringOffset[i] = s.readUInt()
            for (i in chunk.styleOffset.indices) chunk.styleOffset[i] = s.readUInt()
            // String Content
            chunk.strings = arrayOfNulls(chunk.stringCount.toInt())
            chunk.stringLens = IntArray(chunk.stringCount.toInt())
            val builder = StringBuilder()
            for (i in 0 until chunk.stringCount) {
                builder.setLength(0)
                chunk.stringLens[i.toInt()] = s.readUShort()
                val len = chunk.stringLens[i.toInt()] // The leading two bytes are length of string
                for (j in 0 until len) builder.append(s.readChar16())
                @Suppress("UNUSED_VARIABLE") val end0x0000 = s.readChar16()
                chunk.strings[i.toInt()] = builder.toString()
            }
            // Style content
            chunk.styles = arrayOfNulls(chunk.styleCount.toInt())
            chunk.styleLens = IntArray(chunk.styleCount.toInt())
            for (i in 0 until chunk.styleCount) {
                builder.setLength(0)
                chunk.styleLens[i.toInt()] = s.readUShort()
                val len = chunk.styleLens[i.toInt()] // The leading two bytes are length of string
                for (j in 0 until len) builder.append(s.readChar16())
                @Suppress("UNUSED_VARIABLE") val end0x00 = s.readChar16()
                chunk.styles[i.toInt()] = builder.toString()
            }
            return chunk
        }
    }
}
