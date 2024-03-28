package com.newolf.widget.animation.io

import android.annotation.SuppressLint
import java.nio.ByteBuffer
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
open class ByteBufferWriter : Writer {
    protected var byteBuffer: ByteBuffer? = null


    init {
        reset(10 * 1024)
    }

    override fun putByte(b: Byte) {
        byteBuffer!!.put(b)
    }

    override fun putBytes(b: ByteArray?) {
        byteBuffer!!.put(b)
    }

    override fun position(): Int {
        return byteBuffer!!.position()
    }

    override fun skip(length: Int) {
        byteBuffer!!.position(length + position())
    }

    override fun toByteArray(): ByteArray {
        return byteBuffer!!.array()
    }

    override fun close() {}

    override fun reset(size: Int) {
        if (byteBuffer == null || size > byteBuffer!!.capacity()) {
            byteBuffer = ByteBuffer.allocate(size)
            byteBuffer?.order(ByteOrder.LITTLE_ENDIAN)
        }
        byteBuffer?.clear()
    }
}