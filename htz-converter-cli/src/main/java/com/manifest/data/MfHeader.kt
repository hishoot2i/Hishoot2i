package com.manifest.data

import com.common.PrintUtil
import com.manifest.stream.MfStreamer

class MfHeader {
    var magicNumber: Long = 0
    @JvmField
    var fileLength: Long = 0
    override fun toString(): String = """
        -- File Header --
        Magic Number: ${PrintUtil.hex4(magicNumber)}
        File Length: ${PrintUtil.hex4(fileLength)}

    """.trimIndent()

    companion object {
        const val LENGTH = 8

        @JvmStatic
        fun parseFrom(s: MfStreamer): MfHeader {
            val header = MfHeader()
            header.magicNumber = s.readUInt()
            header.fileLength = s.readUInt()
            return header
        }
    }
}
