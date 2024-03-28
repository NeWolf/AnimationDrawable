package com.newolf.widget.drawable.apng.io

import android.text.TextUtils
import com.newolf.widget.animation.io.FilterReader
import com.newolf.widget.animation.io.Reader
import java.io.IOException

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
class APNGReader(reader: Reader) : FilterReader(reader) {
    private val Bytes: ThreadLocal<ByteArray> = ThreadLocal<ByteArray>()


    protected fun ensureBytes(): ByteArray {
        var bytes = Bytes.get()
        if (bytes == null) {
            bytes = ByteArray(4)
            Bytes.set(bytes)
        }
        return bytes
    }


    @Throws(IOException::class)
    fun readInt(): Int {
        val buf = ensureBytes()
        read(buf, 0, 4)
        return buf[3].toInt() and 0xFF or (
                buf[2].toInt() and 0xFF shl 8) or (
                buf[1].toInt() and 0xFF shl 16) or (
                buf[0].toInt() and 0xFF shl 24)
    }

    @Throws(IOException::class)
    fun readShort(): Short {
        val buf = ensureBytes()
        read(buf, 0, 2)
        return (buf[1].toInt() and 0xFF or (
                buf[0].toInt() and 0xFF shl 8)).toShort()
    }

    /**
     * @return read FourCC and match chars
     */
    @Throws(IOException::class)
    fun matchFourCC(chars: String): Boolean {
        if (TextUtils.isEmpty(chars) || chars.length != 4) {
            return false
        }
        val fourCC = readFourCC()
        for (i in 0..3) {
            if (fourCC shr i * 8 and 0xff != chars[i].code) {
                return false
            }
        }
        return true
    }

    @Throws(IOException::class)
    fun readFourCC(): Int {
        val buf = ensureBytes()
        read(buf, 0, 4)
        return buf[0].toInt() and 0xff or (buf[1].toInt() and 0xff shl 8) or (buf[2].toInt() and 0xff shl 16) or (buf[3].toInt() and 0xff shl 24)
    }
}