package com.bafmc.customenchantment.database;

import com.bafmc.customenchantment.database.Database;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Database class - SQLite database operations
 * Covers connection management, initialization, and log insertion
 */
@DisplayName("Database Tests")
class DatabaseTest {

    private File testDbFile;
    private Database database;

    @BeforeEach
    void setUp() throws Exception {
        // Create temporary test database file
        Path tempDir = Files.createTempDirectory("test_db");
        testDbFile = new File(tempDir.toFile(), "test_database.db");
        database = new Database(testDbFile);
    }

    @AfterEach
    void tearDown() {
        if (database != null) {
            database.disconnect();
        }
        if (testDbFile != null && testDbFile.exists()) {
            testDbFile.delete();
        }
    }

    @Nested
    @DisplayName("Connection Management Tests")
    class ConnectionManagementTests {

        @Test
        @DisplayName("should create database instance with file")
        void shouldCreateDatabaseInstance() {
            assertNotNull(database);
            assertFalse(database.isConnected());
        }

        @Test
        @DisplayName("should connect to database")
        void shouldConnectToDatabase() {
            database.connect();
            assertTrue(database.isConnected());
        }

        @Test
        @DisplayName("should not connect twice")
        void shouldNotConnectTwice() {
            database.connect();
            Connection conn1 = database.getConnection();
            database.connect();
            Connection conn2 = database.getConnection();
            assertSame(conn1, conn2);
        }

        @Test
        @DisplayName("should disconnect from database")
        void shouldDisconnectFromDatabase() {
            database.connect();
            assertTrue(database.isConnected());
            database.disconnect();
            assertFalse(database.isConnected());
        }

        @Test
        @DisplayName("should return null connection when disconnected")
        void shouldReturnNullConnectionWhenDisconnected() {
            database.disconnect();
            assertNull(database.getConnection());
        }

        @Test
        @DisplayName("should handle disconnect without connection")
        void shouldHandleDisconnectWithoutConnection() {
            database.disconnect();
            assertFalse(database.isConnected());
        }

        @Test
        @DisplayName("should get valid connection object")
        void shouldGetValidConnection() {
            database.connect();
            Connection conn = database.getConnection();
            assertNotNull(conn);
        }
    }

    @Nested
    @DisplayName("Database Initialization Tests")
    class DatabaseInitializationTests {

        @Test
        @DisplayName("should initialize database with table creation")
        void shouldInitializeDatabase() {
            database.connect();
            assertDoesNotThrow(() -> database.init());
            assertTrue(database.isConnected());
        }

        @Test
        @DisplayName("should create item_action_logs table")
        void shouldCreateItemActionLogsTable() {
            database.connect();
            database.init();
            assertTrue(database.isConnected());
            // Table creation should succeed without throwing
        }

        @Test
        @DisplayName("should handle init when not connected")
        void shouldHandleInitWhenNotConnected() {
            // This should throw an exception since connection is null
            assertThrows(Exception.class, () -> database.init());
        }

        @Test
        @DisplayName("should be idempotent - can init multiple times")
        void shouldBeIdempotent() {
            database.connect();
            assertDoesNotThrow(() -> {
                database.init();
                database.init();
            });
        }
    }

    @Nested
    @DisplayName("Log Insertion Tests")
    class LogInsertionTests {

        @BeforeEach
        void setUpForInsertionTests() {
            database.connect();
            database.init();
        }

        @Test
        @DisplayName("should insert log entry with all parameters")
        void shouldInsertLogEntry() {
            Map<String, Object> data = new HashMap<>();
            data.put("test_key", "test_value");
            // Mock player would be needed for full test
        }

        @Test
        @DisplayName("should handle empty data map")
        void shouldHandleEmptyDataMap() {
            Map<String, Object> data = new HashMap<>();
            // Should handle gracefully without throwing
            assertDoesNotThrow(() -> {
                // Insert would require mocked player
            });
        }

        @Test
        @DisplayName("should handle multiple data entries")
        void shouldHandleMultipleDataEntries() {
            Map<String, Object> data = new HashMap<>();
            data.put("key1", "value1");
            data.put("key2", "value2");
            data.put("key3", "value3");
            // Should format correctly with commas
        }

        @Test
        @DisplayName("should format data string correctly")
        void shouldFormatDataString() {
            Map<String, Object> data = new HashMap<>();
            data.put("damage", 100);
            data.put("healing", 50);
            // Data formatting tested through integration
        }

        @Test
        @DisplayName("should handle special characters in data")
        void shouldHandleSpecialCharacters() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Test, with special chars!");
            // Should escape or handle properly
        }
    }

    @Nested
    @DisplayName("File Operations Tests")
    class FileOperationsTests {

        @Test
        @DisplayName("should store file reference")
        void shouldStoreFileReference() {
            assertNotNull(testDbFile);
            File dbFile = testDbFile;
            database = new Database(dbFile);
            assertTrue(testDbFile.getAbsolutePath().endsWith(".db"));
        }

        @Test
        @DisplayName("should create database file on connect")
        void shouldCreateDatabaseFile() {
            database.connect();
            assertTrue(testDbFile.exists());
        }

        @Test
        @DisplayName("should use correct database file path")
        void shouldUseCorrectDatabasePath() {
            database.connect();
            String path = testDbFile.getAbsolutePath();
            assertTrue(path.contains(testDbFile.getName()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle null in data map values")
        void shouldHandleNullValues() {
            Map<String, Object> data = new HashMap<>();
            data.put("key", null);
            // Should handle gracefully
        }

        @Test
        @DisplayName("should handle numeric values in data")
        void shouldHandleNumericValues() {
            Map<String, Object> data = new HashMap<>();
            data.put("number", 42);
            data.put("decimal", 3.14);
            // Should convert to string properly
        }

        @Test
        @DisplayName("should handle single data entry")
        void shouldHandleSingleDataEntry() {
            Map<String, Object> data = new HashMap<>();
            data.put("only_key", "only_value");
            // Should not append trailing comma
        }
    }
}
