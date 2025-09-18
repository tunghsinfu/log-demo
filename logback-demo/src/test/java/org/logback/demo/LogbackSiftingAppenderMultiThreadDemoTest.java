package org.logback.demo;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * 測試類別 LogbackSiftingAppenderMultiThreadDemoTest
 * 
 * 此類別用於測試 LogbackSiftingAppenderMultiThreadDemo 的多執行緒日誌功能。
 * 
 * 測試流程：
 * 1. 初始化：在測試前，將自定義附加器添加到根記錄器，攔截日誌。
 * 2. 執行測試：執行主程式，模擬多執行緒日誌記錄。
 * 3. 等待條件：使用 Awaitility 等待所有任務完成。
 * 4. 驗證日誌：檢查主執行緒和每個任務的日誌是否正確記錄。
 * 5. 自定義附加器：捕獲日誌訊息，供測試驗證使用。
 */
class LogbackSiftingAppenderMultiThreadDemoTest {

    private TestAppender testAppender;

    /**
     * 測試前的初始化
     * 
     * - 取得 Logback 的根記錄器。
     * - 添加自定義附加器 TestAppender 攔截日誌。
     */
    @BeforeEach
    void setUp() {
        // Attach a custom appender to capture logs
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        testAppender = new TestAppender();
        rootLogger.addAppender(testAppender);
        testAppender.start();
    }

    /**
     * 測試多執行緒日誌功能
     * 
     * - 執行主程式，模擬多執行緒日誌記錄。
     * - 使用 Awaitility 等待最多 15 秒，直到所有任務完成。
     * - 驗證主執行緒和每個任務的日誌是否正確記錄。
     */
    @Test
    void testMultiThreadedLogging() {
        // Run the main method
        LogbackSiftingAppenderMultiThreadDemo.main(new String[] {});

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
     * - TestAppender 是自定義的 Logback 附加器，用於捕獲日誌訊息。
     * - 使用 CopyOnWriteArrayList 儲存日誌訊息，確保多執行緒安全。
     * - 提供 getLogs 方法取得所有捕獲的日誌。
     */
    static class TestAppender extends AppenderBase<ILoggingEvent> {
        private final List<String> logs = new CopyOnWriteArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            logs.add(eventObject.getFormattedMessage());
        }

        public List<String> getLogs() {
            return logs;
        }
    }
}