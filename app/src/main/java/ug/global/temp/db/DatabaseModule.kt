package ug.global.temp.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ug.global.temp.db.dao.UserDao
import ug.global.temp.db.database.AppDatabase
import javax.inject.Singleton

/**
 * Database Module - Hilt Dependency Injection for Room
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
        // Note: AppDatabase.getInstance already handles the Room.databaseBuilder logic
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    // Add other DAOs here as you create them
}
