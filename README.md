# Android Starter App

A ready-to-use Android starter application with bottom sheet login, Material Design components, and Volley for network requests.

## Features

- ✅ Bottom Sheet Login Screen
- ✅ Material Design 3 Components
- ✅ Volley Network Library Integration
- ✅ Utility Helpers for common Android tasks
- ✅ Session Management
- ✅ Network Status Checking
- ✅ Form Validation
- ✅ Easy Customization

## Project Structure

```
app/src/main/java/ug/global/temp/
├── MainActivity.kt                 # Main activity with bottom sheet login
└── util/
    ├── URLS.kt                     # API endpoints and app configuration
    ├── Helpers.kt                  # Common Android utility functions
    └── VolleyHelper.kt             # Simplified Volley network requests
```

## How to Customize for Your App

### 1. Change Package Name

**Option A: Using Android Studio (Recommended)**

1. Right-click on the package name `ug.global.temp` in the project tree
2. Select `Refactor` → `Rename`
3. Choose "Rename package"
4. Enter your new package name (e.g., `com.yourcompany.yourapp`)
5. Click "Refactor"
6. Update the following files manually:
   - `app/build.gradle.kts` → Update `namespace` and `applicationId`
   - `URLS.kt` → Update `AppConfig.PACKAGE_NAME`

**Option B: Manual Method**

1. Update package in `app/build.gradle.kts`:

   ```kotlin
   android {
       namespace = "com.yourcompany.yourapp"

       defaultConfig {
           applicationId = "com.yourcompany.yourapp"
       }
   }
   ```

2. Rename package directories:

   ```
   app/src/main/java/ug/global/temp → app/src/main/java/com/yourcompany/yourapp
   ```

3. Update package declarations in all `.kt` files:

   ```kotlin
   package com.yourcompany.yourapp
   ```

4. Update imports:
   ```kotlin
   import com.yourcompany.yourapp.util.*
   import com.yourcompany.yourapp.databinding.*
   ```

### 2. Change App Name

1. Update `app/src/main/res/values/strings.xml`:

   ```xml
   <string name="app_name">Your App Name</string>
   ```

2. Update `URLS.kt`:
   ```kotlin
   const val APP_NAME = "Your App Name"
   ```

### 3. Configure API Endpoints

Edit `app/src/main/java/ug/global/temp/util/URLS.kt`:

```kotlin
// Update base URL
private const val BASE_URL = "https://your-api.com/api/v1/"

// Add your endpoints
const val YOUR_ENDPOINT = "${CURRENT_BASE_URL}your/endpoint"
```

### 4. Switch API Environments

In `URLS.kt`, change:

```kotlin
const val CURRENT_BASE_URL = DEV_URL  // or STAGING_URL or BASE_URL
```

## API Integration

### Making Network Requests

The app uses `VolleyHelper` for simplified network requests:

#### GET Request

```kotlin
VolleyHelper.get(
    context = this,
    url = URLS.YOUR_ENDPOINT,
    onSuccess = { response ->
        // Handle success
    },
    onError = { error ->
        // Handle error
    }
)
```

#### POST Request

```kotlin
val data = JSONObject().apply {
    put("key", "value")
}

VolleyHelper.post(
    context = this,
    url = URLS.YOUR_ENDPOINT,
    data = data,
    onSuccess = { response ->
        // Handle success
    },
    onError = { error ->
        // Handle error
    }
)
```

## utility functions

    onError = { error ->
        // Handle error
    }

)

````

## Bluetooth Printing

The app includes a flexible Bluetooth printing architecture that supports both generic Bluetooth printers (via SPP/RfComm) and specific Woosim printers (via provided JAR).

### Architecture

The printing logic is abstracted behind the `PrinterService` interface:

```kotlin
interface PrinterService {
    fun connect(device: BluetoothDevice, onConnected: () -> Unit, onError: (String) -> Unit)
    fun disconnect()
    fun printText(text: String)
    fun printImage(imagePath: String)
    fun isConnected(): Boolean
}
````

### Implementations

1.  **GenericBluetoothPrinterService**: Uses standard Android `BluetoothSocket` to communicate using SPP (Serial Port Profile). This works with most thermal printers.
2.  **WoosimPrinterImpl**: Wraps the proprietary `WoosimLib` for specific Woosim printer features.

### Switching Implementations

You can switch between the generic and proprietary implementations in `PrinterModule.kt`.

**To use Generic Printing (Default):**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {
    @Provides
    @Singleton
    fun providePrinterService(impl: GenericBluetoothPrinterService): PrinterService {
        return impl
    }
}
```

**To use Woosim Printing:**

1. Open `PrinterModule.kt`.
2. Change the provider to return `WoosimPrinterImpl`.
   _Note: WoosimImpl requires a `Handler`, so you may need to update the module to provide one._

### Usage

Inject `PrinterService` into your classes (Activities, ViewModels, etc.) using Hilt:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val printerService: PrinterService
) : ViewModel() {

    fun printReceipt(device: BluetoothDevice) {
        printerService.connect(device,
            onConnected = {
                printerService.printText("Hello World\n\n\n")
            },
            onError = { error ->
                // Handle connection error
            }
        )
    }
}
```

## Utility Functions

The `Helpers` object provides many useful functions:

### Toast & Snackbar

```kotlin
Helpers.showToast(context, "Message")
Helpers.showSnackbar(view, "Message")
```

### Dialogs

```kotlin
Helpers.showAlertDialog(context, "Title", "Message")
Helpers.showConfirmationDialog(context, "Title", "Message",
    onConfirm = { /* action */ })
```

### SharedPreferences

```kotlin
Helpers.saveString(context, "key", "value")
val value = Helpers.getString(context, "key")
```

### Network Checking

```kotlin
if (Helpers.isNetworkAvailable(context)) {
    // Make network request
}
```

### Validation

```kotlin
Helpers.isValidEmail(email)
Helpers.isValidPassword(password)
```

### Authentication

```kotlin
Helpers.saveAuthToken(context, token)
Helpers.isLoggedIn(context)
Helpers.logout(context)
```

## Dependencies

This starter app uses:

- **Material Components** - Modern UI design
- **Volley** - Network requests
- **ViewBinding** - Type-safe view access
- **ConstraintLayout** - Flexible layouts

## Building and Running

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or device

## Minimum Requirements

- **minSdk**: 33 (Android 13)
- **targetSdk**: 36
- **Kotlin**: 1.9+
- **Java**: 11

## Customizing the Login Bottom Sheet

The login bottom sheet is defined in:

- Layout: `app/src/main/res/layout/bottom_sheet_login.xml`
- Logic: `MainActivity.kt` → `showLoginBottomSheet()`

To customize:

1. Edit the layout file to add/remove fields
2. Update validation logic in `validateLoginForm()`
3. Modify the login API call in `performLogin()`

## Expected Login API Response

The app expects a JSON response like:

```json
{
  "token": "your_auth_token",
  "user_id": "123",
  "email": "user@example.com"
}
```

Customize in `handleLoginSuccess()` if your API returns different fields.

## Next Steps

1. Create additional activities/fragments for your app
2. Add more API endpoints in `URLS.kt`
3. Implement forgot password functionality
4. Add registration screen
5. Customize the theme in `res/values/themes.xml`
6. Update app icon and splash screen

## License

This is a starter template - feel free to use it for any project!

## Support

For issues or questions, refer to the Android documentation:

- [Android Developers](https://developer.android.com/)
- [Material Design](https://material.io/develop/android)
- [Volley Documentation](https://developer.android.com/training/volley)
