package ug.global.temp.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension Functions - Kotlin extensions for cleaner code
 * 
 * This file contains extension functions that make Android development
 * more concise and readable. Use these throughout your app.
 */

// ==================== VIEW EXTENSIONS ====================

/**
 * Show view (set visibility to VISIBLE)
 */
fun View.show() {
    isVisible = true
}

/**
 * Hide view (set visibility to GONE)
 */
fun View.hide() {
    isVisible = false
}

/**
 * Make view invisible (set visibility to INVISIBLE)
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Toggle view visibility between VISIBLE and GONE
 */
fun View.toggleVisibility() {
    isVisible = !isVisible
}

/**
 * Enable view
 */
fun View.enable() {
    isEnabled = true
    alpha = 1.0f
}

/**
 * Disable view
 */
fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

/**
 * Set click listener with lambda
 */
inline fun View.onClick(crossinline action: () -> Unit) {
    setOnClickListener { action() }
}

/**
 * Set long click listener with lambda
 */
inline fun View.onLongClick(crossinline action: () -> Boolean) {
    setOnLongClickListener { action() }
}


// ==================== CONTEXT EXTENSIONS ====================

/**
 * Show short toast
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Show long toast
 */
fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Get color from resources
 */
fun Context.getColorCompat(colorRes: Int): Int {
    return androidx.core.content.ContextCompat.getColor(this, colorRes)
}

/**
 * Get drawable from resources
 */
fun Context.getDrawableCompat(drawableRes: Int) =
    androidx.core.content.ContextCompat.getDrawable(this, drawableRes)


// ==================== FRAGMENT EXTENSIONS ====================

/**
 * Show toast from fragment
 */
fun Fragment.toast(message: String) {
    requireContext().toast(message)
}

/**
 * Show long toast from fragment
 */
fun Fragment.longToast(message: String) {
    requireContext().longToast(message)
}

/**
 * Hide keyboard from fragment
 */
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}


// ==================== ACTIVITY EXTENSIONS ====================

/**
 * Hide keyboard
 */
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

/**
 * Hide keyboard from specific view
 */
fun Activity.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Show keyboard for specific view
 */
fun Activity.showKeyboard(view: View) {
    view.requestFocus()
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}


// ==================== STRING EXTENSIONS ====================

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is a valid phone number
 */
fun String.isValidPhone(): Boolean {
    return android.util.Patterns.PHONE.matcher(this).matches()
}

/**
 * Check if string is a valid URL
 */
fun String.isValidUrl(): Boolean {
    return android.util.Patterns.WEB_URL.matcher(this).matches()
}

/**
 * Capitalize first letter
 */
fun String.capitalizeFirst(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

/**
 * Truncate string to specified length
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length > maxLength) {
        substring(0, maxLength) + ellipsis
    } else {
        this
    }
}

/**
 * Check if string is empty or blank
 */
fun String?.isNullOrEmpty(): Boolean {
    return this == null || this.trim().isEmpty()
}

/**
 * Convert string to Date
 */
fun String.toDate(pattern: String = "yyyy-MM-dd"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}


// ==================== DATE EXTENSIONS ====================

/**
 * Format date to string
 */
fun Date.format(pattern: String = "dd MMM yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

/**
 * Check if date is today
 */
fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@isToday }
    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

/**
 * Check if date is yesterday
 */
fun Date.isYesterday(): Boolean {
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    val date = Calendar.getInstance().apply { time = this@isYesterday }
    return yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
           yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

/**
 * Get relative time (e.g., "2 hours ago")
 */
fun Date.timeAgo(): String {
    val diff = System.currentTimeMillis() - time
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> format("dd MMM yyyy")
    }
}


// ==================== IMAGEVIEW EXTENSIONS ====================

/**
 * Load image from URL using Coil
 */
fun ImageView.loadUrl(
    url: String?,
    placeholder: Int? = null,
    error: Int? = null
) {
    load(url) {
        crossfade(true)
        placeholder?.let { placeholder(it) }
        error?.let { error(it) }
    }
}

/**
 * Load circular image from URL
 */
fun ImageView.loadCircularUrl(
    url: String?,
    placeholder: Int? = null,
    error: Int? = null
) {
    load(url) {
        crossfade(true)
        transformations(CircleCropTransformation())
        placeholder?.let { placeholder(it) }
        error?.let { error(it) }
    }
}


// ==================== SNACKBAR EXTENSIONS ====================

/**
 * Show snackbar on view
 */
fun View.snackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).show()
}

/**
 * Show snackbar with action
 */
fun View.snackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    Snackbar.make(this, message, duration)
        .setAction(actionText) { action() }
        .show()
}


// ==================== NUMBER EXTENSIONS ====================

/**
 * Format number as currency
 */
fun Number.toCurrency(currencySymbol: String = "$"): String {
    return "$currencySymbol%.2f".format(this.toDouble())
}

/**
 * Format bytes to human readable size
 */
fun Long.toHumanReadableSize(): String {
    return when {
        this < 1024 -> "$this B"
        this < 1024 * 1024 -> "${this / 1024} KB"
        this < 1024 * 1024 * 1024 -> "${this / (1024 * 1024)} MB"
        else -> "${this / (1024 * 1024 * 1024)} GB"
    }
}


// ==================== COLLECTION EXTENSIONS ====================

/**
 * Check if list is null or empty
 */
fun <T> List<T>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

/**
 * Get safe item from list (returns null if index out of bounds)
 */
fun <T> List<T>.getSafe(index: Int): T? {
    return getOrNull(index)
}
