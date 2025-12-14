package ug.global.temp.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ug.global.temp.db.converters.Converters
import ug.global.temp.db.dao.UserDao
import ug.global.temp.db.entities.User

/**
 * AppDatabase - Main Room Database
 * 
 * This is the main database class for your application.
 * Configure database settings, entities, DAOs, and migrations here.
 * 
 * Customization:
 * 1. Change DATABASE_NAME to your app's database name
 * 2. Add new entities to the entities array
 * 3. Add new DAOs as abstract functions
 * 4. Increment version when schema changes
 * 5. Add migrations when upgrading
 */
@Database(
    entities = [
        User::class,
        // Add more entities here
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    // ==================== DAOs ====================
    abstract fun userDao(): UserDao
    // Add more DAOs here
    
    
    companion object {
        // Database name - CUSTOMIZE THIS
        private const val DATABASE_NAME = "app_database.db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get database instance (Singleton pattern)
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Build database with configuration
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // Migrations - Add migrations here when upgrading
                // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                
                // Callback for database creation/opening
                .addCallback(DatabaseCallback())
                
                // DEVELOPMENT ONLY - Remove in production!
                // This will delete and recreate database on schema change
                .fallbackToDestructiveMigration()
                
                .build()
        }
        
        /**
         * Clear database instance (useful for testing)
         */
        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
        
        
        // ==================== MIGRATIONS ====================
        
        /**
         * Example Migration from version 1 to 2
         * 
         * Usage:
         * 1. Increment database version to 2
         * 2. Uncomment this migration
         * 3. Add to buildDatabase: .addMigrations(MIGRATION_1_2)
         */
        /*
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Add a new column
                database.execSQL(
                    "ALTER TABLE users ADD COLUMN newColumn TEXT DEFAULT '' NOT NULL"
                )
            }
        }
        */
        
        /**
         * Example Migration from version 2 to 3
         */
        /*
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Create a new table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS new_table (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        created_at INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        */
    }
    
    
    /**
     * Database Callback - Executes on database creation/opening
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Called when database is created for the first time
            // You can pre-populate data here
        }
        
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Called every time database is opened
        }
        
        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            // Called when fallbackToDestructiveMigration is triggered
        }
    }
}
