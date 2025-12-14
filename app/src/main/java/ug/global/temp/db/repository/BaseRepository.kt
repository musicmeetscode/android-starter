package ug.global.temp.db.repository

import kotlinx.coroutines.flow.Flow
import ug.global.temp.db.dao.BaseDao

/**
 * Base Repository
 * 
 * Generic repository providing common data operations.
 * Your repositories can extend this class to inherit basic CRUD operations.
 * 
 * The repository pattern provides:
 * - Abstraction between data source and business logic
 * - Centralized data access logic
 * - Easy to test and maintain
 * - Can combine multiple data sources (DB + Network)
 * 
 * Usage:
 * class YourRepository(private val dao: YourDao) : BaseRepository<YourEntity>(dao) {
 *     // Add custom repository methods here
 * }
 */
open class BaseRepository<T>(
    private val dao: BaseDao<T>
) {
    
    /**
     * Insert a single entity
     * @return Row ID of inserted entity
     */
    suspend fun insert(entity: T): Long {
        return dao.insert(entity)
    }
    
    /**
     * Insert multiple entities
     * @return List of row IDs
     */
    suspend fun insertAll(vararg entities: T): List<Long> {
        return dao.insertAll(*entities)
    }
    
    /**
     * Insert list of entities
     * @return List of row IDs
     */
    suspend fun insertList(entities: List<T>): List<Long> {
        return dao.insertList(entities)
    }
    
    /**
     * Update an entity
     * @return Number of rows updated
     */
    suspend fun update(entity: T): Int {
        return dao.update(entity)
    }
    
    /**
     * Update multiple entities
     * @return Number of rows updated
     */
    suspend fun updateAll(vararg entities: T): Int {
        return dao.updateAll(*entities)
    }
    
    /**
     * Delete an entity
     * @return Number of rows deleted
     */
    suspend fun delete(entity: T): Int {
        return dao.delete(entity)
    }
    
    /**
     * Delete multiple entities
     * @return Number of rows deleted
     */
    suspend fun deleteAll(vararg entities: T): Int {
        return dao.deleteAll(*entities)
    }
    
    /**
     * Upsert (Insert or Update) an entity
     * @return Row ID
     */
    suspend fun upsert(entity: T): Long {
        return dao.upsert(entity)
    }
    
    /**
     * Upsert multiple entities
     * @return List of row IDs
     */
    suspend fun upsertAll(vararg entities: T): List<Long> {
        return dao.upsertAll(*entities)
    }
}
