package ug.global.temp.db.repository

import kotlinx.coroutines.flow.Flow
import ug.global.temp.db.dao.UserDao
import ug.global.temp.db.entities.User
import java.util.Date

import javax.inject.Inject

/**
 * User Repository - Example repository demonstrating best practices
 */
class UserRepository @Inject constructor(
    private val userDao: UserDao
) : BaseRepository<User>(userDao) {
    
    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Get all users (reactive)
     */
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    /**
     * Get all users (one-time)
     */
    suspend fun getAllUsersList(): List<User> = userDao.getAllUsersList()
    
    /**
     * Get user by ID (reactive)
     */
    fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)
    
    /**
     * Get user by ID (one-time)
     */
    suspend fun getUserByIdOnce(userId: Long): User? = userDao.getUserByIdOnce(userId)
    
    /**
     * Get user by email
     */
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    /**
     * Get user by username
     */
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    /**
     * Get user by auth token
     */
    suspend fun getUserByToken(token: String): User? = userDao.getUserByToken(token)
    
    
    // ==================== SEARCH ====================
    
    /**
     * Search users by name or username
     */
    fun searchUsers(query: String): Flow<List<User>> = userDao.searchUsers(query)
    
    /**
     * Search users by email
     */
    fun searchUsersByEmail(query: String): Flow<List<User>> = userDao.searchUsersByEmail(query)
    
    
    // ==================== FILTERED QUERIES ====================
    
    /**
     * Get verified users
     */
    fun getVerifiedUsers(): Flow<List<User>> = userDao.getVerifiedUsers()
    
    /**
     * Get active users
     */
    fun getActiveUsers(): Flow<List<User>> = userDao.getActiveUsers()
    
    
    // ==================== COUNT & EXISTS ====================
    
    /**
     * Get total user count
     */
    suspend fun getUserCount(): Int = userDao.getUserCount()
    
    /**
     * Get user count (reactive)
     */
    fun getUserCountFlow(): Flow<Int> = userDao.getUserCountFlow()
    
    /**
     * Check if email exists
     */
    suspend fun emailExists(email: String): Boolean = userDao.emailExists(email)
    
    /**
     * Check if username exists
     */
    suspend fun usernameExists(username: String): Boolean = userDao.usernameExists(username)
    
    
    // ==================== BUSINESS LOGIC ====================
    
    /**
     * Register a new user
     * Validates email and username uniqueness
     */
    suspend fun registerUser(
        email: String,
        username: String,
        fullName: String,
        password: String? = null
    ): Result<User> {
        return try {
            // Check if email already exists
            if (emailExists(email)) {
                return Result.failure(Exception("Email already registered"))
            }
            
            // Check if username already exists
            if (usernameExists(username)) {
                return Result.failure(Exception("Username already taken"))
            }
            
            // Create new user
            val user = User(
                email = email,
                username = username,
                fullName = fullName,
                createdAt = Date(),
                updatedAt = Date()
            )
            
            // Insert user
            val userId = insert(user)
            
            // Get and return inserted user
            val insertedUser = getUserByIdOnce(userId)
            if (insertedUser != null) {
                Result.success(insertedUser)
            } else {
                Result.failure(Exception("Failed to retrieve user after registration"))
            }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Login user - Update last login time
     */
    suspend fun loginUser(email: String, authToken: String): Result<User> {
        return try {
            val user = getUserByEmail(email)
                ?: return Result.failure(Exception("User not found"))
            
            // Update auth token and last login
            userDao.updateAuthToken(user.id, authToken)
            userDao.updateLastLogin(user.id, System.currentTimeMillis())
            
            // Get updated user
            val updatedUser = getUserByIdOnce(user.id)
            if (updatedUser != null) {
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("Failed to retrieve user after login"))
            }
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Logout user - Clear auth token
     */
    suspend fun logoutUser(userId: Long): Result<Boolean> {
        return try {
            val result = userDao.updateAuthToken(userId, "")
            if (result > 0) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to logout user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateProfile(
        userId: Long,
        fullName: String? = null,
        phoneNumber: String? = null,
        bio: String? = null,
        profilePictureUrl: String? = null
    ): Result<User> {
        return try {
            val user = getUserByIdOnce(userId)
                ?: return Result.failure(Exception("User not found"))
            
            val updatedUser = user.copy(
                fullName = fullName ?: user.fullName,
                phoneNumber = phoneNumber ?: user.phoneNumber,
                bio = bio ?: user.bio,
                profilePictureUrl = profilePictureUrl ?: user.profilePictureUrl,
                updatedAt = Date()
            )
            
            update(updatedUser)
            Result.success(updatedUser)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verify user email
     */
    suspend fun verifyEmail(userId: Long): Result<Boolean> {
        return try {
            val result = userDao.updateEmailVerificationStatus(userId, true)
            Result.success(result > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Soft delete user
     */
    suspend fun softDeleteUser(userId: Long): Result<Boolean> {
        return try {
            val result = userDao.softDeleteUser(userId)
            Result.success(result > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Restore deleted user
     */
    suspend fun restoreUser(userId: Long): Result<Boolean> {
        return try {
            val result = userDao.restoreUser(userId)
            Result.success(result > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Permanently delete user
     */
    suspend fun permanentlyDeleteUser(userId: Long): Result<Boolean> {
        return try {
            val result = userDao.hardDeleteUser(userId)
            Result.success(result > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete all users (use with caution!)
     */
    suspend fun deleteAllUsers(): Result<Boolean> {
        return try {
            userDao.deleteAllUsers()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
