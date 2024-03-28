package com.newolf.widget.drawable.apng.decoder

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
class IENDChunk : Chunk() {
    companion object{
        val ID = fourCCToInt("IEND")
    }
}