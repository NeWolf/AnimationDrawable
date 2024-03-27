package com.newolf.widget.animation.decode

import java.nio.ByteBuffer

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
interface RenderListener {
    /**
     * 播放开始
     */
    fun onStart()

    /**
     * 帧播放
     */
    fun onRender(byteBuffer: ByteBuffer)

    /**
     * 播放结束
     */
    fun onEnd()
}