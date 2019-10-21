package com.esserver.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
	// 可回收的线程池对象
	private static ExecutorService cachedThreadPool;
	static {
		cachedThreadPool = Executors.newCachedThreadPool();
	}
	private ThreadPoolUtil() {};
	
	/**
	 * 通过线程池执行多线程方法
	 * @param run
	 */
	public static void execute(Runnable run) {
		if (cachedThreadPool == null) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}
		cachedThreadPool.execute(run);
	}
}
