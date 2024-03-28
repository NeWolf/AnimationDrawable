package com.newolf.widget.drawable.apng.decoder

import android.text.TextUtils
import com.newolf.widget.drawable.apng.io.APNGReader
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
open class Chunk {
    var length:Int = 0
    var fourcc = 0
    var crc = 0
    var offset = 0

companion object{
    fun fourCCToInt(fourCC: String): Int {
        return if (TextUtils.isEmpty(fourCC) || fourCC.length != 4) {
            -0x45210001
        } else (fourCC[0].code and 0xff
                or (fourCC[1].code and 0xff shl 8
                ) or (fourCC[2].code and 0xff shl 16
                ) or (fourCC[3].code and 0xff shl 24))
    }
}

    @Throws(IOException::class)
    fun parse(reader: APNGReader) {
        val available: Int = reader.available()
        innerParse(reader)
        val offset: Int = available - reader.available()
        if (offset > length) {
            throw IOException("Out of chunk area")
        } else if (offset < length) {
            reader.skip((length - offset).toLong())
        }
    }

    @Throws(IOException::class)
    open fun innerParse(reader: APNGReader) {
    }
}