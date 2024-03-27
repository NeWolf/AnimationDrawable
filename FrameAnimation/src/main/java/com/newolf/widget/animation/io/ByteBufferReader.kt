package com.newolf.widget.animation.io

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-26
 *
 * =======================================================================
 */
class ByteBufferReader(private val byteBuffer:ByteBuffer ) : Reader {


init {
    byteBuffer.position(0)
}

    @Throws(IOException::class)
    override fun skip(total: Long): Long {
        byteBuffer.position((byteBuffer.position() + total).toInt())
        return total
    }

    @Throws(IOException::class)
    override fun peek(): Byte {
        return byteBuffer.get()
    }

    @Throws(IOException::class)
    override fun reset() {
        byteBuffer.position(0)
    }

    override fun position(): Int {
        return byteBuffer.position()
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray?, start: Int, byteCount: Int): Int {
        byteBuffer[buffer!!, start, byteCount]
        return byteCount
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return byteBuffer.limit() - byteBuffer.position()
    }

    @Throws(IOException::class)
    override fun close() {
    }

    @Throws(IOException::class)
    override fun toInputStream(): InputStream {
        return ByteArrayInputStream(byteBuffer.array())
    }

    fun getByteBuffer(): ByteBuffer {
        return byteBuffer
    }
}