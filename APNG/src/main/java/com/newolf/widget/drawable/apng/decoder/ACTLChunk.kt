package com.newolf.widget.drawable.apng.decoder

import com.newolf.widget.drawable.apng.io.APNGReader
import java.io.IOException

/**
 * ======================================================================
 *
 * @Description: https://developer.mozilla.org/en-US/docs/Mozilla/Tech/APNG#.27acTL.27:_The_Animation_Control_Chunk
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-27
 *
 * =======================================================================
 */
class ACTLChunk: Chunk() {
    companion object{
        val ID = fourCCToInt("acTL")
    }
    var num_frames = 0
    var num_plays = 0

    @Throws(IOException::class)
    override fun innerParse(reader: APNGReader) {
        num_frames = reader.readInt()
        num_plays = reader.readInt()
    }
}