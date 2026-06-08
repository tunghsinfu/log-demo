package org.log4j2.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Log4j2 RegexFilter Demo 的單元測試
 * 
 * 測試重點：
 * 1. 驗證 RegexFilter 是否正確過濾敏感詞彙
 * 2. 驗證 RegexFilter 是否只記錄匹配的日誌
 * 3. 驗證多個 Filter 組合的效果
 */
public class Log4j2RegexFilterDemoTest {

    @BeforeAll
    public static void setup() {
        // 設定 Log4j2 配置檔位置
        System.setProperty("log4j.configurationFile", "log4j2-regex-filter.xml");
        
        // 確保 logs 目錄存在
        Path logsDir = Paths.get("logs");
        if (!Files.exists(logsDir)) {
            try {
                Files.createDirectories(logsDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRegexFilterDemo() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 驗證日誌檔案是否被創建
        assertTrue(Files.exists(Paths.get("logs/regex-filtered.log")), 
            "過濾敏感詞彙的日誌檔應該被創建");
        assertTrue(Files.exists(Paths.get("logs/regex-error-code.log")), 
            "錯誤代碼日誌檔應該被創建");
        assertTrue(Files.exists(Paths.get("logs/regex-email-only.log")), 
            "Email 日誌檔應該被創建");
        assertTrue(Files.exists(Paths.get("logs/regex-sql.log")), 
            "SQL 日誌檔應該被創建");
    }

    @Test
    public void testSensitiveWordFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取過濾後的日誌檔
        Path filteredLogPath = Paths.get("logs/regex-filtered.log");
        if (Files.exists(filteredLogPath)) {
            List<String> lines = Files.readAllLines(filteredLogPath);
            
            // 驗證不包含敏感詞彙
            for (String line : lines) {
                assertFalse(line.toLowerCase().contains("password"), 
                    "過濾後的日誌不應包含 password");
                assertFalse(line.toLowerCase().contains("credit card"), 
                    "過濾後的日誌不應包含 credit card");
            }
            
            // 驗證應該包含正常的日誌
            boolean hasNormalLog = lines.stream()
                .anyMatch(line -> line.contains("normal log message"));
            assertTrue(hasNormalLog, "應該包含正常的日誌訊息");
        }
    }

    @Test
    public void testErrorCodeFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取錯誤代碼日誌檔
        Path errorCodeLogPath = Paths.get("logs/regex-error-code.log");
        if (Files.exists(errorCodeLogPath)) {
            List<String> lines = Files.readAllLines(errorCodeLogPath);
            
            // 驗證所有行都包含錯誤代碼格式
            for (String line : lines) {
                assertTrue(line.matches(".*(?:ERROR|ERR)-\\d{3,}.*"), 
                    "每一行都應該包含錯誤代碼格式: " + line);
            }
        }
    }

    @Test
    public void testEmailFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取 Email 日誌檔
        Path emailLogPath = Paths.get("logs/regex-email-only.log");
        if (Files.exists(emailLogPath)) {
            List<String> lines = Files.readAllLines(emailLogPath);
            
            // 驗證所有行都包含 Email
            for (String line : lines) {
                assertTrue(line.matches(".*[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}.*"), 
                    "每一行都應該包含 Email 地址: " + line);
            }
        }
    }

    @Test
    public void testSqlFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取 SQL 日誌檔
        Path sqlLogPath = Paths.get("logs/regex-sql.log");
        if (Files.exists(sqlLogPath)) {
            List<String> lines = Files.readAllLines(sqlLogPath);
            
            // 驗證所有行都包含 SQL 關鍵字
            for (String line : lines) {
                String lowerLine = line.toLowerCase();
                assertTrue(lowerLine.contains("select") || 
                          lowerLine.contains("insert") || 
                          lowerLine.contains("update") || 
                          lowerLine.contains("delete"), 
                    "每一行都應該包含 SQL 關鍵字: " + line);
            }
        }
    }

    @Test
    public void testNumberIdFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取數字 ID 日誌檔
        Path numberIdLogPath = Paths.get("logs/regex-number-id.log");
        if (Files.exists(numberIdLogPath)) {
            List<String> lines = Files.readAllLines(numberIdLogPath);
            
            // 驗證所有行都包含數字 ID 格式
            for (String line : lines) {
                assertTrue(line.matches(".*(?:#\\d+|ID:\\s*\\w+-?\\d+).*"), 
                    "每一行都應該包含數字 ID: " + line);
            }
        }
    }

    @Test
    public void testCombinedFiltering() throws Exception {
        // 執行主程式
        Log4j2RegexFilterDemo.main(null);
        
        // 等待日誌寫入完成
        Thread.sleep(1000);
        
        // 讀取組合過濾的日誌檔
        Path combinedLogPath = Paths.get("logs/regex-combined.log");
        if (Files.exists(combinedLogPath)) {
            List<String> lines = Files.readAllLines(combinedLogPath);
            
            // 驗證不包含敏感詞彙
            for (String line : lines) {
                assertFalse(line.toLowerCase().contains("password"), 
                    "組合過濾的日誌不應包含 password");
                assertFalse(line.toLowerCase().contains("credit card"), 
                    "組合過濾的日誌不應包含 credit card");
            }
            
            // 驗證不包含 IP 地址
            for (String line : lines) {
                assertFalse(line.matches(".*\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b.*"), 
                    "組合過濾的日誌不應包含 IP 地址: " + line);
            }
            
            // 驗證所有行都包含 ERROR、WARN、exception 或 failed
            for (String line : lines) {
                String lowerLine = line.toLowerCase();
                assertTrue(lowerLine.contains("error") || 
                          lowerLine.contains("warn") || 
                          lowerLine.contains("exception") || 
                          lowerLine.contains("failed"), 
                    "組合過濾的日誌應該包含錯誤相關關鍵字: " + line);
            }
        }
    }
}
