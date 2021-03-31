package com.manifest.stream

class LittleEndianStreamer : MfStreamer() {
    override fun readUInt(): Long = super.readUnsignedInt(Endian.Little)

    override fun readUShort(): Int = super.readUnsignedShort(Endian.Little)

    override fun readChar8(): Char = super.readChar8(Endian.Little)

    override fun readChar16(): Char = super.readChar16(Endian.Little)
}
