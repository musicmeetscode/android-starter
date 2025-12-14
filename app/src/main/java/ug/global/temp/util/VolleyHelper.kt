package ug.global.temp.util

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/**
 * VolleyHelper - Simplified interface for Volley network requests
 * 
 * This singleton class provides easy-to-use methods for making network requests
 * using the Volley library.
 */
object VolleyHelper {
    
    private var requestQueue: RequestQueue? = null
    
    /**
     * Initialize Volley RequestQueue
     */
    fun init(context: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
    }
    
    /**
     * Get RequestQueue instance
     */
    private fun getRequestQueue(context: Context): RequestQueue {
        if (requestQueue == null) {
            init(context)
        }
        return requestQueue!!
    }
    
    /**
     * Add request to queue
     */
    private fun <T> addToRequestQueue(context: Context, request: Request<T>) {
        getRequestQueue(context).add(request)
    }
    
    /**
     * Get headers with authentication token
     */
    private fun getHeaders(context: Context, additionalHeaders: Map<String, String>? = null): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        
        // Add auth token if available
        val token = Helpers.getAuthToken(context)
        if (token.isNotEmpty()) {
            headers[URLS.AppConfig.HEADER_AUTHORIZATION] = "Bearer $token"
        }
        
        // Add content type
        headers[URLS.AppConfig.HEADER_CONTENT_TYPE] = URLS.AppConfig.CONTENT_TYPE_JSON
        headers[URLS.AppConfig.HEADER_ACCEPT] = URLS.AppConfig.CONTENT_TYPE_JSON
        
        // Add any additional headers
        additionalHeaders?.let { headers.putAll(it) }
        
        return headers
    }
    
    
    // ==================== JSON REQUESTS ====================
    
    /**
     * Make a GET request that returns JSON
     */
    fun get(
        context: Context,
        url: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit,
        headers: Map<String, String>? = null
    ) {
        val request = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                val errorMessage = error.message ?: "Network error occurred"
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return getHeaders(context, headers)
            }
        }
        
        addToRequestQueue(context, request)
    }
    
    /**
     * Make a POST request with JSON body
     */
    fun post(
        context: Context,
        url: String,
        data: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit,
        headers: Map<String, String>? = null
    ) {
        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            data,
            Response.Listener { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                val errorMessage = parseError(error)
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return getHeaders(context, headers)
            }
        }
        
        addToRequestQueue(context, request)
    }
    
    /**
     * Make a PUT request with JSON body
     */
    fun put(
        context: Context,
        url: String,
        data: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit,
        headers: Map<String, String>? = null
    ) {
        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            data,
            Response.Listener { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                val errorMessage = parseError(error)
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return getHeaders(context, headers)
            }
        }
        
        addToRequestQueue(context, request)
    }
    
    /**
     * Make a DELETE request
     */
    fun delete(
        context: Context,
        url: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit,
        headers: Map<String, String>? = null
    ) {
        val request = object : JsonObjectRequest(
            Method.DELETE,
            url,
            null,
            Response.Listener { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                val errorMessage = parseError(error)
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return getHeaders(context, headers)
            }
        }
        
        addToRequestQueue(context, request)
    }
    
    
    // ==================== STRING REQUESTS ====================
    
    /**
     * Make a GET request that returns String
     */
    fun getString(
        context: Context,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        headers: Map<String, String>? = null
    ) {
        val request = object : StringRequest(
            Method.GET,
            url,
            Response.Listener { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                val errorMessage = parseError(error)
                onError(errorMessage)
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return getHeaders(context, headers)
            }
        }
        
        addToRequestQueue(context, request)
    }
    
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Parse error from Volley error
     */
    private fun parseError(error: com.android.volley.VolleyError): String {
        return when {
            error.networkResponse != null -> {
                val statusCode = error.networkResponse.statusCode
                val data = error.networkResponse.data
                
                if (data != null) {
                    try {
                        val errorJson = JSONObject(String(data))
                        errorJson.optString("message", "Error: $statusCode")
                    } catch (e: Exception) {
                        "Error: $statusCode"
                    }
                } else {
                    "Error: $statusCode"
                }
            }
            error.message != null -> error.message!!
            else -> "Network error occurred"
        }
    }
    
    /**
     * Cancel all pending requests
     */
    fun cancelAllRequests(context: Context) {
        getRequestQueue(context).cancelAll { true }
    }
    
    /**
     * Cancel requests with specific tag
     */
    fun cancelRequests(context: Context, tag: String) {
        getRequestQueue(context).cancelAll(tag)
    }
}
