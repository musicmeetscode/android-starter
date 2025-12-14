package ug.global.temp.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ug.global.temp.db.entities.User

/**
 * User DAO - Data Access Object for User entity
 * 
 * This DAO demonstrates:
 * - Extending BaseDao for common operations
 * - Custom query methods
 * - Flow for reactive data
 * - Suspend functions for coroutines
 * - Different query patterns
 * 
 * Use this as a template for creating other DAOs
 */
@Dao
interface UserDao : BaseDao<User> {
    
    // ==================== QUERY ALL ====================
    
    /**
     * Get all users (excluding soft-deleted)
     * Flow automatically updates when data changes
     */
    @Query("SELECT * FROM users WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * Get all users including deleted ones
     */
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersIncludingDeleted(): Flow<List<User>>
    
    /**
     * Get all users as list (one-time fetch)
     */
    @Query("SELECT * FROM users WHERE isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getAllUsersList(): List<User>
    
    
    // ==================== QUERY BY ID ====================
    
    /**
     * Get user by ID (reactive)
     */
    @Query("SELECT * FROM users WHERE id = :userId AND isDeleted = 0 LIMIT 1")
    fun getUserById(userId: Long): Flow<User?>
    
    /**
     * Get user by ID (one-time fetch)
     */
    @Query("SELECT * FROM users WHERE id = :userId AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByIdOnce(userId: Long): User?
    
    
    // ==================== QUERY BY UNIQUE FIELDS ====================
    
    /**
     * Get user by email
     */
    @Query("SELECT * FROM users WHERE email = :email AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    /**
     * Get user by email (reactive)
     */
    @Query("SELECT * FROM users WHERE email = :email AND isDeleted = 0 LIMIT 1")
    fun getUserByEmailFlow(email: String): Flow<User?>
    
    /**
     * Get user by username
     */
    @Query("SELECT * FROM users WHERE username = :username AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
    
    /**
     * Get user by auth token
     */
    @Query("SELECT * FROM users WHERE authToken = :token AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByToken(token: String): User?
    
    
    // ==================== SEARCH ====================
    
    /**
     * Search users by name or username
     */
    @Query("""
        SELECT * FROM users 
        WHERE (fullName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%')
        AND isDeleted = 0 
        ORDER BY fullName ASC
    """)
    fun searchUsers(query: String): Flow<List<User>>
    
    /**
     * Search users by email
     */
    @Query("""
        SELECT * FROM users 
        WHERE email LIKE '%' || :query || '%'
        AND isDeleted = 0 
        ORDER BY email ASC
    """)
    fun searchUsersByEmail(query: String): Flow<List<User>>
    
    
    // ==================== FILTERED QUERIES ====================
    
    /**
     * Get verified users only
     */
    @Query("SELECT * FROM users WHERE isEmailVerified = 1 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getVerifiedUsers(): Flow<List<User>>
    
    /**
     * Get active users (verified and not deleted)
     */
    @Query("SELECT * FROM users WHERE isEmailVerified = 1 AND isDeleted = 0 ORDER BY lastLoginAt DESC")
    fun getActiveUsers(): Flow<List<User>>
    
    /**
     * Get users created after a certain date
     */
    @Query("SELECT * FROM users WHERE createdAt > :timestamp AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getUsersCreatedAfter(timestamp: Long): Flow<List<User>>
    
    
    // ==================== COUNT & EXISTS ====================
    
    /**
     * Get total user count
     */
    @Query("SELECT COUNT(*) FROM users WHERE isDeleted = 0")
    suspend fun getUserCount(): Int
    
    /**
     * Get total user count (reactive)
     */
    @Query("SELECT COUNT(*) FROM users WHERE isDeleted = 0")
    fun getUserCountFlow(): Flow<Int>
    
    /**
     * Check if email exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email AND isDeleted = 0 LIMIT 1)")
    suspend fun emailExists(email: String): Boolean
    
    /**
     * Check if username exists
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username AND isDeleted = 0 LIMIT 1)")
    suspend fun usernameExists(username: String): Boolean
    
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update user token
     */
    @Query("UPDATE users SET authToken = :token, updatedAt = :timestamp WHERE id = :userId")
    suspend fun updateAuthToken(userId: Long, token: String, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Update email verification status
     */
    @Query("UPDATE users SET isEmailVerified = :isVerified, updatedAt = :timestamp WHERE id = :userId")
    suspend fun updateEmailVerificationStatus(userId: Long, isVerified: Boolean, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Update last login time
     */
    @Query("UPDATE users SET lastLoginAt = :loginTime, updatedAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, loginTime: Long, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Update profile picture
     */
    @Query("UPDATE users SET profilePictureUrl = :url, updatedAt = :timestamp WHERE id = :userId")
    suspend fun updateProfilePicture(userId: Long, url: String, timestamp: Long = System.currentTimeMillis()): Int
    
    
    // ==================== SOFT DELETE ====================
    
    /**
     * Soft delete user by ID
     */
    @Query("UPDATE users SET isDeleted = 1, updatedAt = :timestamp WHERE id = :userId")
    suspend fun softDeleteUser(userId: Long, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Restore soft deleted user
     */
    @Query("UPDATE users SET isDeleted = 0, updatedAt = :timestamp WHERE id = :userId")
    suspend fun restoreUser(userId: Long, timestamp: Long = System.currentTimeMillis()): Int
    
    /**
     * Get soft deleted users
     */
    @Query("SELECT * FROM users WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedUsers(): Flow<List<User>>
    
    
    // ==================== HARD DELETE ====================
    
    /**
     * Permanently delete user by ID
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun hardDeleteUser(userId: Long): Int
    
    /**
     * Permanently delete all soft-deleted users
     */
    @Query("DELETE FROM users WHERE isDeleted = 1")
    suspend fun purgeDeletedUsers(): Int
    
    /**
     * Delete all users (use with caution!)
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers(): Int
    
    
    // ==================== TRANSACTION EXAMPLES ====================
    
    /**
     * Insert user and return the user with generated ID
     */
    @Transaction
    suspend fun insertAndGet(user: User): User? {
        val id = insert(user)
        return getUserByIdOnce(id)
    }
    
    /**
     * Update user's updated timestamp
     */
    @Transaction
    suspend fun updateWithTimestamp(user: User): Int {
        val updatedUser = user.copy(updatedAt = java.util.Date())
        return update(updatedUser)
    }
}
