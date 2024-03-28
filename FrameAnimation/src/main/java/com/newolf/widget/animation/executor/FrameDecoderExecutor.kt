package com.newolf.widget.animation.executor

import android.os.HandlerThread
import android.os.Looper
import com.newolf.widget.animation.utils.DebugLog
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
        if (taskId < 0) {
            return Looper.getMainLooper()
        }
        val idx = taskId % POOL_NUMBER
       DebugLog.dTag(TAG,"taskId = $taskId , idx = $idx")
        return if (idx >= mHandlerThreadGroup.size) {
            val handlerThread = HandlerThread("FrameDecoderExecutor-$idx")
            handlerThread.start()
            mHandlerThreadGroup.add(handlerThread)
            val looper = handlerThread.getLooper()
            looper ?: Looper.getMainLooper()
        } else {
            run {
                val looper: Looper = mHandlerThreadGroup[idx].getLooper()
                looper?:Looper.getMainLooper()
            }
        }
    }

    companion object{
        const val TAG = "FrameDecoderExecutor"

        private val sInstance = FrameDecoderExecutor()
        const val POOL_NUMBER = 4

        fun getInstance():FrameDecoderExecutor{
            return sInstance
        }
    }


    private val mHandlerThreadGroup = ArrayList<HandlerThread>(POOL_NUMBER)
    private val counter = AtomicInteger(0)

}