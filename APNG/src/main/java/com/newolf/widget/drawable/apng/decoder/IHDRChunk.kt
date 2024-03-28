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
class IHDRChunk: Chunk() {
    companion object{
        val ID = fourCCToInt("IHDR")
    }

    /**
     * 图像宽度，以像素为单位
     */
    var width = 0

    /**
     * 图像高度，以像素为单位
     */
    var height = 0

    var data = ByteArray(5)

    @Throws(IOException::class)
    override fun innerParse(reader: APNGReader) {
        width = reader.readInt()
        height = reader.readInt()
        reader.read(data, 0, data.size)
    }
}