package com.newolf.widget.drawable.apng.io

import com.newolf.widget.animation.io.ByteBufferWriter
import java.nio.ByteOrder

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-27
 *
 * =======================================================================
 */
class APNGWriter: ByteBufferWriter() {
    fun writeFourCC(`val`: Int) {
        putByte((`val` and 0xff).toByte())
        putByte((`val` shr 8 and 0xff).toByte())
        putByte((`val` shr 16 and 0xff).toByte())
        putByte((`val` shr 24 and 0xff).toByte())
    }

    fun writeInt(`val`: Int) {
        putByte((`val` shr 24 and 0xff).toByte())
        putByte((`val` shr 16 and 0xff).toByte())
        putByte((`val` shr 8 and 0xff).toByte())
        putByte((`val` and 0xff).toByte())
    }

    override fun reset(size: Int) {
        super.reset(size)
        byteBuffer!!.order(ByteOrder.BIG_ENDIAN)
    }
}