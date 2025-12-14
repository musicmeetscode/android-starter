package ug.global.temp.network

import com.google.gson.annotations.SerializedName

/**
 * API Response Models
 * 
 * Data classes for API responses. Add your API models here.
 */

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: T?
)

/**
 * Login Request
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Login Response
 */
data class LoginResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("full_name")
    val fullName: String?
)

/**
 * User Response
 */
data class UserResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("full_name")
    val fullName: String?,
    
    @SerializedName("profile_picture")
    val profilePicture: String?,
    
    @SerializedName("created_at")
    val createdAt: String
)

/**
 * Error Response
 */
data class ErrorResponse(
    @SerializedName("error")
    val error: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("code")
    val code: Int?
)
