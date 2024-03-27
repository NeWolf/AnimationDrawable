package com.newolf.widget.animation.executor

import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.atomic.AtomicInteger

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
class FrameDecoderExecutor private constructor() {
    fun generateTaskId(): Int {
        return counter.getAndDecrement()
    }

    fun getLooper(taskId: Int): Looper {
        val idx = taskId % POOL_NUMBER
        return if (idx >= mHandlerThreadGroup.size) {
            val handlerThread = HandlerThread("FrameDecoderExecutor-$idx")
            handlerThread.start()
            mHandlerThreadGroup.add(handlerThread)
            val looper = handlerThread.getLooper()
            looper ?: Looper.getMainLooper()
        } else {
            run {
                val looper: Looper = mHandlerThreadGroup[idx].getLooper()
                looper ?: Looper.getMainLooper()
            }
        }
    }

    companion object{
        private val sInstance = FrameDecoderExecutor()
        const val POOL_NUMBER = 4

        fun getInstance():FrameDecoderExecutor{
            return sInstance
        }
    }


    private val mHandlerThreadGroup = ArrayList<HandlerThread>(POOL_NUMBER)
    private val counter = AtomicInteger(0)

}