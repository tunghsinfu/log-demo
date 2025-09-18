package org.log4j2.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.ThreadContext;

public class Log4jRoutingAppenderMultiThreadDemo {

	private static final Logger logger = LoggerFactory.getLogger(Log4jRoutingAppenderMultiThreadDemo.class);

	// 封裝 Runnable 的類
	static class Task implements Runnable {

		private static final Logger logger = LoggerFactory.getLogger(Log4jRoutingAppenderMultiThreadDemo.Task.class);

		private String taskName;

		// 建構子，允許傳入任務名稱
		public Task(String taskName) {
			this.taskName = taskName;
		}

		@Override
		public void run() {
			try {
				ThreadContext.put("myRouteKey", taskName); // 🔑 設定 ThreadContext key
				logger.info(Thread.currentThread().getName() + " [" + taskName + "] is executing");
				long sleepMillis = ThreadLocalRandom.current().nextLong(3_000); // 小於 3 秒
				Thread.sleep(sleepMillis);
				logger.info(Thread.currentThread().getName() + " [" + taskName + "] is executed");
			} catch (InterruptedException e) {
				logger.info(Thread.currentThread().getName() + " [" + taskName + "] is stopped");
			} finally {
				ThreadContext.clearAll(); // 避免 thread 污染
			}
			logger.info(Thread.currentThread().getName() + " [" + taskName + "] is finished");
		}
	}

	public static void main(String[] args) {

		logger.info("main thread start");

		// 建立一個 cached thread pool
		ExecutorService executor = Executors.newFixedThreadPool(3);

		// 提交 5 個任務
		for (int i = 1; i <= 5; i++) {
			int taskNumber = i;
			Task task = new Task("Task " + taskNumber);
			executor.execute(task);
		}

		// 關閉 executor（會等待所有任務完成）
		executor.shutdown();

		logger.info("main thread end");

	}
}
