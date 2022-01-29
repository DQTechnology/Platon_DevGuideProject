package com.digquant.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,
            10,
            10 * 60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>());

    public static void AddTask(Runnable runnable) {
        executor.execute(runnable);
    }
}
