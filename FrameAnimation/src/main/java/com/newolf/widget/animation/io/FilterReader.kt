package com.newolf.widget.animation.io

import java.io.InputStream

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
open class FilterReader(protected var reader: Reader):Reader {
    override fun skip(total: Long): Long {
      return  reader.skip(total)
    }

    override fun peek(): Byte {
        return reader.peek()

    }

    override fun reset() {
        reader.reset()
    }

    override fun position(): Int {
        return reader.position()
    }

    override fun read(buffer: ByteArray?, start: Int, byteCount: Int): Int {
        return reader.read(buffer, start, byteCount)
    }

    override fun available(): Int {
       return reader.available()
    }

    override fun toInputStream(): InputStream? {
        reset()
        return reader.toInputStream()
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     *
     *  As noted in [AutoCloseable.close], cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * *mark* the `Closeable` as closed, prior to throwing
     * the `IOException`.
     *
     * @throws IOException if an I/O error occurs
     */
    override fun close() {
       reader.close()
    }
}