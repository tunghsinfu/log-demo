package org.log4j2.demo;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Log4jRoutingAppenderMultiThreadDemoTest {

	private TestAppender testAppender;

	@BeforeEach
	void setUp() {
	    Logger rootLogger = (Logger) LogManager.getRootLogger();
	    testAppender = new TestAppender("TestAppender");
	    rootLogger.addAppender(testAppender);
	    testAppender.start();
	}

	@Test
	void testMultiThreadedLogging() {
		// Run the main method
		Log4jRoutingAppenderMultiThreadDemo.main(new String[] {});

		// Wait until all tasks are logged
		await().atMost(15, TimeUnit.SECONDS)
				.until(() -> testAppender.getLogs().stream().filter(log -> log.contains("is finished")).count() == 5);

		// Verify main thread logs
		assertTrue(testAppender.getLogs().stream().anyMatch(log -> log.contains("main thread start")));
		assertTrue(testAppender.getLogs().stream().anyMatch(log -> log.contains("main thread end")));

		// Verify task logs
		for (int i = 1; i <= 5; i++) {
			String taskName = "Task " + i;
			assertTrue(
					testAppender.getLogs().stream().anyMatch(log -> log.contains("[" + taskName + "] is executing")));
			assertTrue(testAppender.getLogs().stream().anyMatch(log -> log.contains("[" + taskName + "] is executed")));
			assertTrue(testAppender.getLogs().stream().anyMatch(log -> log.contains("[" + taskName + "] is finished")));
		}
	}

	/**
	 * 自定義日誌附加器
	 * 
	 * - TestAppender 是自定義的 Log4j2 附加器，用於捕獲日誌訊息。 - 使用 CopyOnWriteArrayList
	 * 儲存日誌訊息，確保多執行緒安全。 - 提供 getLogs 方法取得所有捕獲的日誌。
	 */
	static class TestAppender extends AbstractAppender {

		private final List<String> logs = new CopyOnWriteArrayList<>();

		protected TestAppender(String name) {
			super(name, null, null, true, null);
		}

		@Override
		public void append(LogEvent event) {
			logs.add(event.getMessage().getFormattedMessage());
		}

		public List<String> getLogs() {
			return logs;
		}
	}
}