package com.newolf.widget.animation.io

import com.newolf.widget.animation.utils.DebugLog
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.max

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
class StreamReader(`in`: InputStream) :FilterInputStream(`in`) ,Reader{
    companion object{
        const val TAG = "StreamReader"
    }

    private var position = 0
    init {
        try {
            `in`.reset()
        }catch (e:Exception){
            DebugLog.eTag(TAG,"try reset has exception",e)
        }
    }
    override fun peek(): Byte {
        val ret = read().toByte()
        position++
        return ret
    }

    override fun position(): Int {
        return position
    }

    override fun toInputStream(): InputStream {
        return this
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray?, start: Int, byteCount: Int): Int {
        val ret = super.read(buffer, start, byteCount)
        position += max(0, ret)
        return ret
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        super.reset()
        position = 0
    }

    @Throws(IOException::class)
    override fun skip(total: Long): Long {
        var toSkip = total
        while (toSkip > 0) {
            val skipped = super.skip(toSkip)
            if (skipped > 0) {
                toSkip -= skipped
            } else {
                // Skip has no specific contract as to what happens when you reach the end of
                // the stream. To differentiate between temporarily not having more data and
                // having finished the stream, we read a single byte when we fail to skip any
                // amount of data.
                val testEofByte = super.read()
                if (testEofByte == -1) {
                    break
                } else {
                    toSkip--
                }
            }
        }
        position += (total - toSkip).toInt()
        return total - toSkip
    }


}