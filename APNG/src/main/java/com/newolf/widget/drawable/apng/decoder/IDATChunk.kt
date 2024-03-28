package com.newolf.widget.drawable.apng.decoder

/**
 * ======================================================================
 *
 * 作用描述
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-27
 *
 * =======================================================================
 */
class IDATChunk: Chunk() {
    companion object{
        val ID = fourCCToInt("IDAT")
    }
}