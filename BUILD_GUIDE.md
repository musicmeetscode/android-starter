# Build and Configuration Guide

## Prerequisites

Before building the project, ensure you have:

1. **Android Studio** (Latest version recommended)
2. **JDK 11 or higher** properly configured
3. **Android SDK** with API level 33+

## Building the Project

### Option 1: Using Android Studio (Recommended)

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `Starter` folder and click "OK"
4. Wait for Gradle sync to complete
5. Click "Build" → "Make Project" or press `Ctrl+F9`
6. Run the app on an emulator or device

### Option 2: Using Command Line

If you encounter Java toolchain issues, set JAVA_HOME:

```bash
# Find your Java installation
java -version

# Set JAVA_HOME (adjust path as needed)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# Build the project
./gradlew build
```

### Common Build Issues

#### Java Toolchain Error

If you see:

```
Toolchain installation does not provide the required capabilities
```

**Solution**:

1. Install JDK 11:

   ```bash
   sudo apt install openjdk-11-jdk
   ```

2. Set JAVA_HOME in `gradle.properties`:
   ```properties
   org.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64
   ```

#### Missing Android SDK

**Solution**: Open Android Studio and let it download the required SDK components.

## Quick Start

### 1. Customize Your App

Run the setup script:

```bash
./setup.sh
```

This will prompt you to enter:

- New package name (e.g., `com.mycompany.myapp`)
- New app name (e.g., `MyApp`)

### 2. Update API Configuration

Edit `app/src/main/java/[your_package]/util/URLS.kt`:

```kotlin
private const val BASE_URL = "https://your-api.com/api/v1/"
```

### 3. Sync and Build

In Android Studio:

1. Click "File" → "Sync Project with Gradle Files"
2. Wait for sync to complete
3. Click "Build" → "Rebuild Project"

### 4. Run the App

1. Connect a device or start an emulator
2. Click the "Run" button (green play icon)
3. The app will install and launch

## Project Structure Verification

After setup, your structure should look like:

```
app/src/main/java/com/yourcompany/yourapp/
├── MainActivity.kt
└── util/
    ├── Helpers.kt
    ├── URLS.kt
    └── VolleyHelper.kt
```

## Testing the Login Flow

The app includes a mock login interface. To test:

1. Launch the app
2. Click the login FAB (floating action button)
3. Enter any email and password (min 6 characters)
4. Click "Login"

**Note**: The login will fail unless you configure a real API endpoint in `URLS.kt`.

## Customization Checklist

- [ ] Run `./setup.sh` to change package and app name
- [ ] Update `URLS.kt` with your API endpoints
- [ ] Customize colors in `res/values/colors.xml`
- [ ] Update app icon in `res/mipmap/`
- [ ] Modify layouts in `res/layout/` as needed
- [ ] Add your business logic in `MainActivity.kt`

## Next Steps

1. **Add more activities**: Right-click on package → New → Activity
2. **Create fragments**: For modular UI components
3. **Add navigation**: Use Navigation Component
4. **Implement data layer**: Room database or Repository pattern
5. **Add dependency injection**: Hilt or Koin
6. **Write tests**: Unit tests and UI tests

## Resources

- [Android Documentation](https://developer.android.com/)
- [Material Design Components](https://material.io/develop/android)
- [Volley Documentation](https://developer.android.com/training/volley)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## Troubleshooting

### App crashes on launch

Check logcat in Android Studio for error messages.

### Network requests not working

1. Verify internet permission in `AndroidManifest.xml`
2. Check API URL in `URLS.kt`
3. Ensure device has internet connection
4. Check API response format matches expected structure

### Layout issues

1. Sync Gradle files
2. Clean project: Build → Clean Project
3. Rebuild: Build → Rebuild Project
4. Invalidate caches: File → Invalidate Caches and Restart

## Support

For Android development help:

- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)
- [Android Developers Community](https://developer.android.com/community)
- [Reddit - r/androiddev](https://reddit.com/r/androiddev)
