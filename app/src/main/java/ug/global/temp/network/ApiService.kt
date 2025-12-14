package ug.global.temp.network

import retrofit2.Response
import retrofit2.http.*

/**
 * API Service - Retrofit interface for API endpoints
 * 
 * Define your API endpoints here using Retrofit annotations.
 * All methods are suspend functions for use with Coroutines.
 */
interface ApiService {
    
    // ==================== AUTHENTICATION ====================
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>
    
    @POST("auth/register")
    suspend fun register(
        @Body request: Map<String, String>
    ): Response<ApiResponse<LoginResponse>>
    
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body email: Map<String, String>
    ): Response<ApiResponse<Any>>
    
    @POST("auth/verify-email")
    suspend fun verifyEmail(
        @Body code: Map<String, String>
    ): Response<ApiResponse<Any>>
    
    
    // ==================== USER ====================
    
    @GET("user/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserResponse>>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Body updates: Map<String, Any>
    ): Response<ApiResponse<UserResponse>>
    
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<UserResponse>>>
    
    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): Response<ApiResponse<UserResponse>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<ApiResponse<Any>>
    
    
    // ==================== DATA ====================
    
    @GET("data")
    suspend fun getData(
        @QueryMap filters: Map<String, String> = emptyMap()
    ): Response<ApiResponse<List<Any>>>
    
    @POST("data")
    suspend fun createData(
        @Body data: Map<String, Any>
    ): Response<ApiResponse<Any>>
    
    @PUT("data/{id}")
    suspend fun updateData(
        @Path("id") dataId: String,
        @Body updates: Map<String, Any>
    ): Response<ApiResponse<Any>>
    
    @DELETE("data/{id}")
    suspend fun deleteData(
        @Path("id") dataId: String
    ): Response<ApiResponse<Any>>
    
    
    // ==================== EXAMPLE: FILE UPLOAD ====================
    
    @Multipart
    @POST("upload")
    suspend fun uploadFile(
        @Part("file") file: okhttp3.MultipartBody.Part,
        @Part("description") description: okhttp3.RequestBody?
    ): Response<ApiResponse<Any>>
    
    
    // Add more endpoints as needed...
}
