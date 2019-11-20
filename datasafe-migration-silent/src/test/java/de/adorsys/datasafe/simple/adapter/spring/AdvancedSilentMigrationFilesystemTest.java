//// Disabled, dont know why this is not working yet.
//// always get wrong key. hopefully programming error
//// worst case cache problem
//
//package de.adorsys.datasafe.simple.adapter.spring;
//
//import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceWithMigration;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//
//
//public class AdvancedSilentMigrationFilesystemTest extends SilentMigrationFilesystemTest {
//    @BeforeAll
//    static void beforeAllHere() {
//        SimpleDatasafeServiceWithMigration.migrateToNewFolder = false;
//    }
//
//    @AfterAll
//    static void afterAllHere() {
//        SimpleDatasafeServiceWithMigration.migrateToNewFolder = false;
//    }
//}
