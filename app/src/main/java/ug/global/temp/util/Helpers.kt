package ug.global.temp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit
import androidx.core.view.isVisible

/**
 * Helpers - Common Android utility functions
 * 
 * This file contains frequently used helper functions for Android development
 * to reduce code duplication and improve maintainability.
 */
object Helpers {
    
    // ==================== TOAST & SNACKBAR HELPERS ====================
    
    /**
     * Show a short toast message
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show a long toast message
     */
    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Show a Snackbar with default duration
     */
    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
    
    /**
     * Show a Snackbar with action button
     */
    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        action: () -> Unit
    ) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(actionText) { action() }
            .show()
    }
    
    
    // ==================== DIALOG HELPERS ====================
    
    /**
     * Show a simple alert dialog
     */
    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        onPositiveClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Show a confirmation dialog with Yes/No buttons
     */
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Yes",
        negativeButtonText: String = "No",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }
            .show()
    }
    
    
    // ==================== SHARED PREFERENCES HELPERS ====================
    
    /**
     * Get SharedPreferences instance
     */
    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(URLS.AppConfig.PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save string to SharedPreferences
     */
    fun saveString(context: Context, key: String, value: String) {
        getPreferences(context).edit().putString(key, value).apply()
    }
    
    /**
     * Get string from SharedPreferences
     */
    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getPreferences(context).getString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * Save boolean to SharedPreferences
     */
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        getPreferences(context).edit { putBoolean(key, value) }
    }
    
    /**
     * Get boolean from SharedPreferences
     */
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }
    
    /**
     * Save int to SharedPreferences
     */
    fun saveInt(context: Context, key: String, value: Int) {
        getPreferences(context).edit { putInt(key, value) }
    }
    
    /**
     * Get int from SharedPreferences
     */
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }
    
    /**
     * Clear all SharedPreferences
     */
    fun clearPreferences(context: Context) {
        getPreferences(context).edit { clear() }
    }
    
    /**
     * Remove specific key from SharedPreferences
     */
    fun removeKey(context: Context, key: String) {
        getPreferences(context).edit().remove(key).apply()
    }
    
    
    // ==================== NETWORK HELPERS ====================
    
    /**
     * Check if device has internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Check network and show error if not available
     */
    fun checkNetworkAndNotify(context: Context, view: View? = null): Boolean {
        if (!isNetworkAvailable(context)) {
            if (view != null) {
                showSnackbar(view, "No internet connection")
            } else {
                showToast(context, "No internet connection")
            }
            return false
        }
        return true
    }
    
    
    // ==================== KEYBOARD HELPERS ====================
    
    /**
     * Hide keyboard
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    
    /**
     * Show keyboard for specific view
     */
    fun showKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    
    
    // ==================== VALIDATION HELPERS ====================
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate phone number (basic validation)
     */
    fun isValidPhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }
    
    /**
     * Check if string is empty or blank
     */
    fun isEmptyOrBlank(text: String?): Boolean {
        return text.isNullOrBlank()
    }
    
    /**
     * Validate password strength (minimum 6 characters)
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }
    
    
    // ==================== DATE & TIME HELPERS ====================
    
    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Format date to string
     */
    fun formatDate(timestamp: Long, pattern: String = "dd MMM yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format time to string
     */
    fun formatTime(timestamp: Long, pattern: String = "hh:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format date and time to string
     */
    fun formatDateTime(timestamp: Long, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    
    // ==================== STRING HELPERS ====================
    
    /**
     * Capitalize first letter of string
     */
    fun capitalizeFirstLetter(text: String): String {
        return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
    
    /**
     * Truncate string to specified length with ellipsis
     */
    fun truncateString(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            "${text.substring(0, maxLength)}..."
        } else {
            text
        }
    }
    
    
    // ==================== VIEW HELPERS ====================
    
    /**
     * Show view
     */
    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }
    
    /**
     * Hide view
     */
    fun hideView(view: View) {
        view.visibility = View.GONE
    }
    
    /**
     * Make view invisible
     */
    fun makeInvisible(view: View) {
        view.visibility = View.INVISIBLE
    }
    
    /**
     * Toggle view visibility
     */
    fun toggleVisibility(view: View) {
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }
    
    
    // ==================== AUTH HELPERS ====================
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(context: Context): Boolean {
        return getBoolean(context, URLS.AppConfig.KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Save login state
     */
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        saveBoolean(context, URLS.AppConfig.KEY_IS_LOGGED_IN, isLoggedIn)
    }
    
    /**
     * Get auth token
     */
    fun getAuthToken(context: Context): String {
        return getString(context, URLS.AppConfig.KEY_AUTH_TOKEN, "")
    }
    
    /**
     * Save auth token
     */
    fun saveAuthToken(context: Context, token: String) {
        saveString(context, URLS.AppConfig.KEY_AUTH_TOKEN, token)
    }
    
    /**
     * Clear auth data and logout
     */
    fun logout(context: Context) {
        removeKey(context, URLS.AppConfig.KEY_AUTH_TOKEN)
        removeKey(context, URLS.AppConfig.KEY_USER_ID)
        removeKey(context, URLS.AppConfig.KEY_USER_EMAIL)
        saveLoginState(context, false)
    }
    
    
    // ==================== DATABASE HELPERS ====================
    
    /**
     * Get database instance
     */
    fun getDatabase(context: Context): ug.global.temp.db.database.AppDatabase {
        return ug.global.temp.db.database.AppDatabase.getInstance(context)
    }
    
    /**
     * Get UserRepository instance
     */
    fun getUserRepository(context: Context): ug.global.temp.db.repository.UserRepository {
        val db = getDatabase(context)
        return ug.global.temp.db.repository.UserRepository(db.userDao())
    }
}
