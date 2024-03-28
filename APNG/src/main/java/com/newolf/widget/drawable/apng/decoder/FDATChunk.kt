package com.newolf.widget.drawable.apng.decoder

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
class FDATChunk: Chunk() {
    companion object{
        val ID = fourCCToInt("fdAT")
    }
    var sequence_number = 0

    @Throws(IOException::class)
    override fun innerParse(reader: APNGReader) {
        sequence_number = reader.readInt()
    }
}