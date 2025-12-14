# Quick Reference - Utility Functions

This is a quick reference guide for all utility functions available in the starter app.

## Table of Contents

- [Network Requests (VolleyHelper)](#network-requests-volleyhelper)
- [Common Helpers](#common-helpers)
- [URLs & Configuration](#urls--configuration)

---

## Network Requests (VolleyHelper)

### Initialize (Call in Application or first Activity)

```kotlin
VolleyHelper.init(this)
```

### GET Request

```kotlin
VolleyHelper.get(
    context = this,
    url = URLS.GET_DATA,
    onSuccess = { response: JSONObject ->
        // Handle JSON response
        val data = response.getString("key")
    },
    onError = { error: String ->
        // Handle error
        Helpers.showToast(this, error)
    }
)
```

### POST Request

```kotlin
val data = JSONObject().apply {
    put("username", "john")
    put("email", "john@example.com")
}

VolleyHelper.post(
    context = this,
    url = URLS.POST_DATA,
    data = data,
    onSuccess = { response ->
        // Handle success
    },
    onError = { error ->
        // Handle error
    }
)
```

### PUT Request

```kotlin
val updateData = JSONObject().apply {
    put("id", 123)
    put("name", "Updated Name")
}

VolleyHelper.put(context, URLS.UPDATE_DATA, updateData, { response ->
    // Success
}, { error ->
    // Error
})
```

### DELETE Request

```kotlin
VolleyHelper.delete(context, URLS.DELETE_DATA, { response ->
    // Success
}, { error ->
    // Error
})
```

### Custom Headers

```kotlin
val customHeaders = mapOf(
    "Custom-Header" to "value",
    "API-Version" to "2.0"
)

VolleyHelper.get(context, url, { response ->
    // Success
}, { error ->
    // Error
}, headers = customHeaders)
```

---

## Common Helpers

### Toast & Snackbar

```kotlin
// Short toast
Helpers.showToast(context, "Hello World")

// Long toast
Helpers.showLongToast(context, "This is a long message")

// Snackbar
Helpers.showSnackbar(view, "Action completed")

// Snackbar with action
Helpers.showSnackbarWithAction(view, "Item deleted", "UNDO") {
    // Undo action
}
```

### Dialogs

```kotlin
// Simple alert
Helpers.showAlertDialog(context, "Title", "Message")

// Alert with action
Helpers.showAlertDialog(context, "Success", "Profile updated", "OK") {
    // Action when OK clicked
}

// Confirmation dialog
Helpers.showConfirmationDialog(
    context = this,
    title = "Delete",
    message = "Are you sure?",
    onConfirm = {
        // User confirmed
    },
    onCancel = {
        // User cancelled (optional)
    }
)
```

### SharedPreferences

```kotlin
// Save data
Helpers.saveString(context, "username", "john_doe")
Helpers.saveBoolean(context, "is_premium", true)
Helpers.saveInt(context, "score", 100)

// Get data
val username = Helpers.getString(context, "username")
val isPremium = Helpers.getBoolean(context, "is_premium")
val score = Helpers.getInt(context, "score")

// Remove specific key
Helpers.removeKey(context, "username")

// Clear all preferences
Helpers.clearPreferences(context)
```

### Network Checking

```kotlin
// Check if network is available
if (Helpers.isNetworkAvailable(context)) {
    // Network available
} else {
    // No network
}

// Check and notify user
if (Helpers.checkNetworkAndNotify(context, view)) {
    // Network available, proceed
} else {
    // User has been notified about no network
}
```

### Keyboard Management

```kotlin
// Hide keyboard
Helpers.hideKeyboard(activity)

// Show keyboard for specific view
Helpers.showKeyboard(context, editText)
```

### Validation

```kotlin
// Email validation
if (Helpers.isValidEmail(email)) {
    // Valid email
}

// Phone validation
if (Helpers.isValidPhone(phone)) {
    // Valid phone
}

// Password validation (min 6 chars by default)
if (Helpers.isValidPassword(password)) {
    // Valid password
}

// Custom password length
if (Helpers.isValidPassword(password, minLength = 8)) {
    // Valid password (min 8 chars)
}

// Check if empty
if (Helpers.isEmptyOrBlank(text)) {
    // Text is empty or blank
}
```

### Date & Time

```kotlin
// Get current timestamp
val now = Helpers.getCurrentTimestamp()

// Format date
val date = Helpers.formatDate(timestamp)  // "14 Dec 2024"
val customDate = Helpers.formatDate(timestamp, "yyyy-MM-dd")  // "2024-12-14"

// Format time
val time = Helpers.formatTime(timestamp)  // "10:30 PM"

// Format date and time
val dateTime = Helpers.formatDateTime(timestamp)  // "14 Dec 2024, 10:30 PM"
```

### String Utilities

```kotlin
// Capitalize first letter
val name = Helpers.capitalizeFirstLetter("john")  // "John"

// Truncate string
val short = Helpers.truncateString("This is a long text", 10)  // "This is a..."
```

### View Utilities

```kotlin
// Show view
Helpers.showView(myView)

// Hide view (GONE)
Helpers.hideView(myView)

// Make invisible
Helpers.makeInvisible(myView)

// Toggle visibility
Helpers.toggleVisibility(myView)
```

### Authentication

```kotlin
// Check if logged in
if (Helpers.isLoggedIn(context)) {
    // User is logged in
}

// Save login state
Helpers.saveLoginState(context, true)

// Save auth token
Helpers.saveAuthToken(context, "your_token_here")

// Get auth token
val token = Helpers.getAuthToken(context)

// Logout (clears all auth data)
Helpers.logout(context)
```

---

## URLs & Configuration

### API Endpoints

```kotlin
// Pre-defined endpoints
URLS.LOGIN
URLS.REGISTER
URLS.LOGOUT
URLS.USER_PROFILE
URLS.GET_DATA
URLS.POST_DATA
// ... see URLS.kt for all endpoints
```

### Configuration

```kotlin
// App settings
URLS.AppConfig.APP_NAME
URLS.AppConfig.PACKAGE_NAME
URLS.AppConfig.API_TIMEOUT
URLS.AppConfig.MAX_RETRIES

// SharedPreferences keys
URLS.AppConfig.KEY_AUTH_TOKEN
URLS.AppConfig.KEY_USER_ID
URLS.AppConfig.KEY_IS_LOGGED_IN

// Headers
URLS.AppConfig.HEADER_AUTHORIZATION
URLS.AppConfig.HEADER_CONTENT_TYPE
```

### Helper Functions

```kotlin
// Build URL with query parameters
val params = mapOf("page" to "1", "limit" to "10")
val url = URLS.getUrlWithParams(URLS.GET_DATA, params)
// Result: "https://api.example.com/data?page=1&limit=10"

// Build URL with path parameters
val url = URLS.getUrlWithPath(URLS.GET_DATA, "123", "details")
// Result: "https://api.example.com/data/123/details"
```

### Environment Switching

In `URLS.kt`, change:

```kotlin
const val CURRENT_BASE_URL = DEV_URL      // Development
const val CURRENT_BASE_URL = STAGING_URL  // Staging
const val CURRENT_BASE_URL = BASE_URL     // Production
```

---

## Common Patterns

### Login Flow

```kotlin
fun login(email: String, password: String) {
    // Validate
    if (!Helpers.isValidEmail(email)) {
        Helpers.showToast(this, "Invalid email")
        return
    }

    // Check network
    if (!Helpers.checkNetworkAndNotify(this, binding.root)) {
        return
    }

    // Prepare data
    val data = JSONObject().apply {
        put("email", email)
        put("password", password)
    }

    // Make request
    VolleyHelper.post(this, URLS.LOGIN, data,
        onSuccess = { response ->
            val token = response.getString("token")
            Helpers.saveAuthToken(this, token)
            Helpers.saveLoginState(this, true)
            Helpers.showToast(this, "Login successful")
        },
        onError = { error ->
            Helpers.showSnackbar(binding.root, "Login failed: $error")
        }
    )
}
```

### Fetching Data

```kotlin
fun loadUserProfile() {
    if (!Helpers.isLoggedIn(this)) {
        // Redirect to login
        return
    }

    VolleyHelper.get(this, URLS.USER_PROFILE,
        onSuccess = { response ->
            val name = response.getString("name")
            val email = response.getString("email")
            // Update UI
            binding.tvName.text = name
            binding.tvEmail.text = email
        },
        onError = { error ->
            Helpers.showSnackbar(binding.root, error)
        }
    )
}
```

### Logout Flow

```kotlin
fun logout() {
    Helpers.showConfirmationDialog(this, "Logout", "Are you sure?",
        onConfirm = {
            // Optional: Call logout API
            VolleyHelper.post(this, URLS.LOGOUT, JSONObject(),
                onSuccess = {
                    Helpers.logout(this)
                    // Navigate to login screen
                },
                onError = {
                    // Still logout locally even if API fails
                    Helpers.logout(this)
                }
            )
        }
    )
}
```

---

## Tips

1. **Always check network** before making requests
2. **Validate input** before sending to API
3. **Handle errors gracefully** with user-friendly messages
4. **Show loading indicators** during network requests
5. **Use SharedPreferences** for small data; use Room for larger datasets
6. **Don't hardcode strings** - use string resources
7. **Use ViewBinding** instead of findViewById

## Need More?

Check the source code:

- `util/Helpers.kt` - All helper functions
- `util/VolleyHelper.kt` - Network request helpers
- `util/URLS.kt` - API configuration
- `MainActivity.kt` - Example usage

Happy coding! 🚀
