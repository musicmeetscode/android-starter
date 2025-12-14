package ug.global.temp.util

/**
 * URLS Configuration
 * 
 * This file contains all API endpoints and configuration URLs for the application.
 * Update BASE_URL to match your backend server.
 */
object URLS {
    
    // ==================== BASE CONFIGURATION ====================
    // TODO: Update this to your production URL
    private const val BASE_URL = "https://api.example.com/api/v1/"
    
    // Alternative URLs for different environments
    private const val DEV_URL = "http://192.168.1.100:8000/api/v1/"
    private const val STAGING_URL = "https://staging-api.example.com/api/v1/"
    
    // Switch between environments
    const val CURRENT_BASE_URL = BASE_URL // Change to DEV_URL or STAGING_URL as needed
    
    
    // ==================== AUTHENTICATION ENDPOINTS ====================
    const val LOGIN = "${CURRENT_BASE_URL}auth/login"
    const val REGISTER = "${CURRENT_BASE_URL}auth/register"
    const val LOGOUT = "${CURRENT_BASE_URL}auth/logout"
    const val FORGOT_PASSWORD = "${CURRENT_BASE_URL}auth/forgot-password"
    const val RESET_PASSWORD = "${CURRENT_BASE_URL}auth/reset-password"
    const val VERIFY_EMAIL = "${CURRENT_BASE_URL}auth/verify-email"
    const val REFRESH_TOKEN = "${CURRENT_BASE_URL}auth/refresh-token"
    
    
    // ==================== USER ENDPOINTS ====================
    const val USER_PROFILE = "${CURRENT_BASE_URL}user/profile"
    const val UPDATE_PROFILE = "${CURRENT_BASE_URL}user/update"
    const val CHANGE_PASSWORD = "${CURRENT_BASE_URL}user/change-password"
    const val DELETE_ACCOUNT = "${CURRENT_BASE_URL}user/delete"
    
    
    // ==================== DATA ENDPOINTS ====================
    const val GET_DATA = "${CURRENT_BASE_URL}data"
    const val POST_DATA = "${CURRENT_BASE_URL}data/create"
    const val UPDATE_DATA = "${CURRENT_BASE_URL}data/update"
    const val DELETE_DATA = "${CURRENT_BASE_URL}data/delete"
    
    
    // ==================== APP CONFIGURATION ====================
    /**
     * App Configuration - Update these values for your specific app
     */
    object AppConfig {
        // App Identity
        const val APP_NAME = "Temp" // TODO: Change this to your app name
        const val PACKAGE_NAME = "ug.global.temp" // TODO: Change this to your package name
        
        // API Settings
        const val API_TIMEOUT = 30000 // 30 seconds
        const val MAX_RETRIES = 3
        
        // SharedPreferences Keys
        const val PREF_NAME = "${PACKAGE_NAME}.prefs"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_REMEMBER_ME = "remember_me"
        
        // Request Headers
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_CONTENT_TYPE = "Content-Type"
        const val HEADER_ACCEPT = "Accept"
        const val CONTENT_TYPE_JSON = "application/json"
    }
    
    
    // ==================== HELPER FUNCTIONS ====================
    /**
     * Get full URL with parameters
     */
    fun getUrlWithParams(baseUrl: String, params: Map<String, String>): String {
        if (params.isEmpty()) return baseUrl
        
        val queryParams = params.entries.joinToString("&") { (key, value) ->
            "$key=$value"
        }
        return "$baseUrl?$queryParams"
    }
    
    /**
     * Get URL with path parameter
     */
    fun getUrlWithPath(baseUrl: String, vararg paths: String): String {
        return paths.fold(baseUrl) { acc, path -> "$acc/$path" }
    }
}
