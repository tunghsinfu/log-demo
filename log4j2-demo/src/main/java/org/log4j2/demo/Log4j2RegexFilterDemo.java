package org.log4j2.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log4j2 RegexFilter Demo
 * 
 * 演示如何使用 Log4j2 的 RegexFilter 來過濾日誌訊息
 * RegexFilter 可以根據正則表達式匹配日誌訊息、Logger 名稱等來決定是否接受或拒絕日誌
 * 
 * 使用方式：
 * 1. 在 log4j2-regex-filter.xml 配置檔中定義 RegexFilter
 * 2. 可以配置在 Appender、Logger 或 Root Logger 上
 * 3. 支援 onMatch 和 onMismatch 兩種動作：ACCEPT、DENY、NEUTRAL
 */
public class Log4j2RegexFilterDemo {

    private static final Logger logger = LoggerFactory.getLogger(Log4j2RegexFilterDemo.class);

    public static void main(String[] args) {
        logger.info("=== Log4j2 RegexFilter Demo Start ===");

        // 測試案例 1: 包含敏感詞彙的日誌（應該被過濾掉）
        logger.info("This is a normal log message");
        logger.warn("Warning: password reset required");  // 包含 password，可能被過濾
        logger.error("Error: credit card validation failed");  // 包含 credit card，可能被過濾
        
        // 測試案例 2: 不同等級的日誌
        logger.debug("Debug message for troubleshooting");
        logger.info("User login successful: user123");
        logger.warn("Session timeout warning");
        logger.error("Database connection failed");
        
        // 測試案例 3: 包含特定格式的日誌（例如：錯誤代碼）
        logger.error("ERROR-001: System initialization failed");
        logger.error("ERROR-002: Configuration file not found");
        logger.info("INFO-001: Application started successfully");
        
        // 測試案例 4: 包含數字的日誌
        logger.info("Processing order #12345");
        logger.info("Transaction ID: TXN-67890");
        
        // 測試案例 5: 包含特殊字元或格式的日誌
        logger.info("User email: user@example.com");
        logger.info("Server IP: 192.168.1.100");
        logger.warn("Invalid request from IP: 10.0.0.1");
        
        // 測試案例 6: 多行日誌或堆疊追蹤
        try {
            throw new RuntimeException("Simulated exception for testing");
        } catch (Exception e) {
            logger.error("Exception occurred during processing", e);
        }
        
        // 測試案例 7: 包含 SQL 或資料庫相關的日誌
        logger.info("Executing SQL: SELECT * FROM users WHERE id = ?");
        logger.debug("SQL execution time: 150ms");
        
        // 測試案例 8: 包含 URL 或路徑的日誌
        logger.info("API request: /api/v1/users/123");
        logger.info("File path: /var/log/application.log");
        
        logger.info("=== Log4j2 RegexFilter Demo End ===");
    }
}
