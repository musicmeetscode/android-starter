package ug.global.temp.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Base DAO Interface
 * 
 * Generic DAO providing common CRUD operations for all entities.
 * Your DAOs can extend this interface to inherit basic operations.
 * 
 * Usage:
 * @Dao
 * interface YourDao : BaseDao<YourEntity> {
 *     // Add custom queries here
 * }
 */
@Dao
interface BaseDao<T> {
    
    /**
     * Insert a single entity
     * @return The row ID of the inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long
    
    /**
     * Insert multiple entities
     * @return List of row IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: T): List<Long>
    
    /**
     * Insert list of entities
     * @return List of row IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entities: List<T>): List<Long>
    
    /**
     * Update an entity
     * @return Number of rows updated
     */
    @Update
    suspend fun update(entity: T): Int
    
    /**
     * Update multiple entities
     * @return Number of rows updated
     */
    @Update
    suspend fun updateAll(vararg entities: T): Int
    
    /**
     * Delete an entity
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(entity: T): Int
    
    /**
     * Delete multiple entities
     * @return Number of rows deleted
     */
    @Delete
    suspend fun deleteAll(vararg entities: T): Int
    
    /**
     * Upsert (Insert or Update) - requires Room 2.5.0+
     */
    @Upsert
    suspend fun upsert(entity: T): Long
    
    /**
     * Upsert multiple entities
     */
    @Upsert
    suspend fun upsertAll(vararg entities: T): List<Long>
}
