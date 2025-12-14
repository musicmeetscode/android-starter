package ug.global.temp.db.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ug.global.temp.db.entities.User
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

/**
 * DatabaseHelper - Utility functions for database operations
 * 
 * Provides helper methods for:
 * - Database initialization
 * - Seeding initial data
 * - Backup and restore
 * - Clearing database
 * - Database file operations
 */
object DatabaseHelper {
    
    /**
     * Initialize database with sample data (for development/testing)
     */
    suspend fun seedDatabase(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        val userDao = db.userDao()
        
        // Check if database already has data
        val userCount = userDao.getUserCount()
        if (userCount > 0) {
            // Database already seeded
            return@withContext
        }
        
        // Create sample users
        val sampleUsers = listOf(
            User(
                email = "admin@example.com",
                username = "admin",
                fullName = "Admin User",
                phoneNumber = "+1234567890",
                bio = "System Administrator",
                isEmailVerified = true,
                createdAt = Date(),
                updatedAt = Date()
            ),
            User(
                email = "john.doe@example.com",
                username = "johndoe",
                fullName = "John Doe",
                phoneNumber = "+1234567891",
                bio = "Software Developer",
                isEmailVerified = true,
                createdAt = Date(),
                updatedAt = Date()
            ),
            User(
                email = "jane.smith@example.com",
                username = "janesmith",
                fullName = "Jane Smith",
                phoneNumber = "+1234567892",
                bio = "Product Manager",
                isEmailVerified = false,
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        
        // Insert sample users
        userDao.insertList(sampleUsers)
    }
    
    /**
     * Clear all data from database
     */
    suspend fun clearDatabase(context: Context) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        
        // Clear all tables
        db.userDao().deleteAllUsers()
        
        // Add more tables here as you create them
        // db.yourDao().deleteAllYourEntities()
    }
    
    /**
     * Get database file path
     */
    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath("app_database.db").absolutePath
    }
    
    /**
     * Get database file size in bytes
     */
    fun getDatabaseSize(context: Context): Long {
        val dbFile = File(getDatabasePath(context))
        return if (dbFile.exists()) {
            dbFile.length()
        } else {
            0
        }
    }
    
    /**
     * Get database file size in human-readable format
     */
    fun getDatabaseSizeFormatted(context: Context): String {
        val bytes = getDatabaseSize(context)
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
    
    /**
     * Check if database exists
     */
    fun databaseExists(context: Context): Boolean {
        val dbFile = File(getDatabasePath(context))
        return dbFile.exists()
    }
    
    /**
     * Backup database to external storage
     * Returns the backup file path or null if failed
     */
    suspend fun backupDatabase(context: Context, backupDir: File): String? = withContext(Dispatchers.IO) {
        try {
            // Close database before backup
            AppDatabase.getInstance(context).close()
            
            val currentDBPath = getDatabasePath(context)
            val currentDB = File(currentDBPath)
            
            if (!currentDB.exists()) {
                return@withContext null
            }
            
            // Create backup directory if it doesn't exist
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            
            // Create backup file with timestamp
            val timestamp = System.currentTimeMillis()
            val backupFile = File(backupDir, "app_database_backup_$timestamp.db")
            
            // Copy database file
            FileInputStream(currentDB).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Reopen database
            AppDatabase.getInstance(context)
            
            backupFile.absolutePath
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Restore database from backup file
     * Returns true if successful
     */
    suspend fun restoreDatabase(context: Context, backupFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!backupFile.exists()) {
                return@withContext false
            }
            
            // Close current database
            AppDatabase.getInstance(context).close()
            AppDatabase.clearInstance()
            
            val currentDBPath = getDatabasePath(context)
            val currentDB = File(currentDBPath)
            
            // Delete current database
            if (currentDB.exists()) {
                currentDB.delete()
            }
            
            // Copy backup to database location
            FileInputStream(backupFile).use { input ->
                FileOutputStream(currentDB).use { output ->
                    input.copyTo(output)
                }
            }
            
            // Reopen database
            AppDatabase.getInstance(context)
            
            true
            
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Delete database file
     */
    fun deleteDatabase(context: Context): Boolean {
        AppDatabase.getInstance(context).close()
        AppDatabase.clearInstance()
        return context.deleteDatabase("app_database.db")
    }
    
    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(context: Context): DatabaseStats = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(context)
        
        DatabaseStats(
            userCount = db.userDao().getUserCount(),
            databaseSize = getDatabaseSize(context),
            databasePath = getDatabasePath(context)
            // Add more stats as you add more tables
        )
    }
    
    /**
     * Export database to SQL statements (simplified version)
     */
    suspend fun exportToSQL(context: Context, outputFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(context)
            val users = db.userDao().getAllUsersList()
            
            outputFile.bufferedWriter().use { writer ->
                writer.write("-- Database Export\n")
                writer.write("-- Generated: ${Date()}\n\n")
                
                // Export users
                writer.write("-- Users Table\n")
                users.forEach { user ->
                    writer.write("INSERT INTO users VALUES (${user.id}, '${user.email}', '${user.username}', ...);\n")
                }
                
                // Add more tables here
            }
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

/**
 * Database Statistics data class
 */
data class DatabaseStats(
    val userCount: Int,
    val databaseSize: Long,
    val databasePath: String
    // Add more fields as needed
) {
    fun getSizeFormatted(): String {
        return when {
            databaseSize < 1024 -> "$databaseSize B"
            databaseSize < 1024 * 1024 -> "${databaseSize / 1024} KB"
            else -> "${databaseSize / (1024 * 1024)} MB"
        }
    }
}
