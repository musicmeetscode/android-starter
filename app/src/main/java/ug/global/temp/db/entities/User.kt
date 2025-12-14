package ug.global.temp.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * User Entity - Sample entity demonstrating Room database usage
 * 
 * This is a complete example showing best practices:
 * - Primary key with auto-generation
 * - Indexed fields for faster queries
 * - Nullable and non-nullable fields
 * - Timestamp tracking
 * - Soft delete support
 * 
 * Customize this or create new entities following this pattern.
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true),
        Index(value = ["isDeleted"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,
    
    val email: String,
    val username: String,
    val fullName: String = "",
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    
    // Authentication
    val authToken: String? = null,
    val isEmailVerified: Boolean = false,
    val lastLoginAt: Date? = null,
    
    // Timestamps
    override val createdAt: Date = Date(),
    override val updatedAt: Date = Date(),
    
    // Soft delete
    override val isDeleted: Boolean = false
) : BaseEntity {
    
    /**
     * Get display name (full name or username)
     */
    fun getDisplayName(): String {
        return fullName.ifBlank { username }
    }
    
    /**
     * Check if user profile is complete
     */
    fun isProfileComplete(): Boolean {
        return email.isNotBlank() && 
               username.isNotBlank() && 
               fullName.isNotBlank()
    }
    
    /**
     * Check if user is active (not deleted and email verified)
     */
    fun isActive(): Boolean {
        return !isDeleted && isEmailVerified
    }
}
