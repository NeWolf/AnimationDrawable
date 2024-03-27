package com.newolf.widget.animation.loader

import com.newolf.widget.animation.io.FileReader
import com.newolf.widget.animation.io.Reader
import java.io.File

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
class FileLoader(private val filePath:String):Loader {
    override fun obtain(): Reader {
        return FileReader(File(filePath))
    }
}