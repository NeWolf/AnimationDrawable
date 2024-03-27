package com.newolf.widget.animation.io

import java.io.File
import java.io.FileInputStream
import java.io.IOException

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
class FileReader(private val file: File):FilterReader(StreamReader(FileInputStream(file))) {
    @Throws(IOException::class)
    override fun reset() {
        reader.close()
        reader = StreamReader(FileInputStream(file))
    }
}