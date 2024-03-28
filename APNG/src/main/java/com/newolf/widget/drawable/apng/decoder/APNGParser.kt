package com.newolf.widget.drawable.apng.decoder

import android.content.Context
import com.newolf.widget.animation.io.Reader
import com.newolf.widget.animation.io.StreamReader
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.io.APNGReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream



/**
 * ======================================================================
 *
 * @link {https://www.w3.org/TR/PNG/#5PNG-file-signature}
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-27
 *
 * =======================================================================
 */
object APNGParser {
    const val TAG = "APNGParser"
    fun isAPNG(filePath: String?): Boolean {
        var inputStream: InputStream? = null
        return try {
            inputStream = FileInputStream(filePath)
            isAPNG(StreamReader(inputStream))
        } catch (e: Exception) {
            false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun isAPNG(context: Context, assetPath: String?): Boolean {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.assets.open(assetPath!!)
            isAPNG(StreamReader(inputStream))
        } catch (e: Exception) {
            false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun isAPNG(context: Context, resId: Int): Boolean {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.resources.openRawResource(resId)
            isAPNG(StreamReader(inputStream))
        } catch (e: Exception) {
            false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun isAPNG(`in`: Reader): Boolean {
        val reader: APNGReader = if (`in` is APNGReader) `in` else APNGReader(`in`)
        try {
            if (!reader.matchFourCC("\u0089PNG") || !reader.matchFourCC("\r\n\u001a\n")) {
                throw FormatException()
            }
            while (reader.available() > 0) {
                val chunk = parseChunk(reader)
                if (chunk is ACTLChunk) {
                    DebugLog.dTag(TAG, "isAPNG = true")
                    return true
                }
            }
        } catch (e: IOException) {
            if (e !is FormatException) {
                e.printStackTrace()
            }
        }
        return false
    }

    @Throws(IOException::class)
    fun parse(reader: APNGReader): List<Chunk> {
        if (!reader.matchFourCC("\u0089PNG") || !reader.matchFourCC("\r\n\u001a\n")) {
            throw FormatException()
        }
        val chunks: MutableList<Chunk> = ArrayList()
        while (reader.available() > 0) {
            chunks.add(parseChunk(reader))
        }
        return chunks
    }

    @Throws(IOException::class)
    private fun parseChunk(reader: APNGReader): Chunk {
        val offset: Int = reader.position()
        val size: Int = reader.readInt()
        val fourCC: Int = reader.readFourCC()
        val chunk: Chunk = when (fourCC) {
            ACTLChunk.ID -> {
                ACTLChunk()
            }
            FCTLChunk.ID -> {
                FCTLChunk()
            }
            FDATChunk.ID -> {
                FDATChunk()
            }
            IDATChunk.ID -> {
                IDATChunk()
            }
            IENDChunk.ID -> {
                IENDChunk()
            }
            IHDRChunk.ID -> {
                IHDRChunk()
            }
            else -> {
                Chunk()
            }
        }
        chunk.offset = offset
        chunk.fourcc = fourCC
        chunk.length = size
        chunk.parse(reader)
        chunk.crc = reader.readInt()
        return chunk
    }

    internal class FormatException : IOException("APNG Format error")
}
