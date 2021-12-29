package com.digquant.util

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadPoolUtil {
    companion object {

        private val executor = ThreadPoolExecutor(
            2,
            10,
            10 * 60,
            TimeUnit.SECONDS,
            LinkedBlockingDeque()
        )

        fun AddTask(runnable: Runnable?) {
            executor.execute(runnable)
        }
    }
}