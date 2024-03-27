package com.newolf.widget.animation.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.newolf.widget.animation.R
import com.newolf.widget.animation.io.Reader
import com.newolf.widget.animation.io.Writer

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

abstract class Frame<R : Reader?, W : Writer?>(reader: R) {
    protected val reader: R
    var frameWidth = 0
    var frameHeight = 0
    var frameX = 0
    var frameY = 0
    var frameDuration = 0
    protected val srcRect = Rect()
    protected val dstRect = Rect()

    init {
        this.reader = reader
    }

    abstract fun draw(
        canvas: Canvas?,
        paint: Paint?,
        sampleSize: Int,
        reusedBitmap: Bitmap?,
        writer: W
    ): Bitmap?
}
