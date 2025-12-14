# Changelog

All notable changes and additions to this Android Starter App.

## [1.0.0] - 2024-12-14

### 🎉 Initial Release

#### Added

##### Core Application Files

- **MainActivity.kt** - Main activity with bottom sheet login functionality
  - Login form validation
  - Network request handling
  - Session management
  - UI state management (logged in/out)
  - Example action buttons with GET/POST requests
  - Logout with confirmation dialog

##### Utility Components (`util/` package)

- **Helpers.kt** - Comprehensive utility functions

  - Toast & Snackbar helpers
  - Dialog helpers (alert, confirmation)
  - SharedPreferences wrapper functions
  - Network availability checking
  - Keyboard management
  - Input validation (email, phone, password)
  - Date & time formatting
  - String utilities
  - View visibility helpers
  - Authentication helpers (login/logout/token management)

- **URLS.kt** - API endpoints and configuration

  - Base URL configuration
  - Environment switching (DEV, STAGING, PROD)
  - Pre-defined authentication endpoints
  - User management endpoints
  - Data CRUD endpoints
  - App configuration constants
  - SharedPreferences key constants
  - URL building helper functions

- **VolleyHelper.kt** - Network request wrapper
  - Simplified GET, POST, PUT, DELETE methods
  - Automatic auth token injection
  - Custom header support
  - Error parsing and handling
  - Request queue management
  - Singleton pattern implementation

##### Layout Files

- **activity_main.xml** - Main activity layout

  - Material Card with app branding
  - Logged-out view with login prompt
  - Logged-in view with action buttons
  - Floating Action Button for login
  - CoordinatorLayout with ScrollView

- **bottom_sheet_login.xml** - Login bottom sheet
  - Material TextInputLayouts for email and password
  - Password visibility toggle
  - Forgot password link
  - Register link
  - Loading progress indicator
  - Material Design 3 components

##### Configuration Files

- **AndroidManifest.xml** - Updated with:

  - Internet and network state permissions
  - MainActivity as launcher activity
  - Cleartext traffic allowed for development
  - Window soft input mode configured

- **build.gradle.kts** (app level) - Updated with:

  - ViewBinding enabled
  - Volley library (1.2.1)
  - Material Components (1.11.0)
  - ConstraintLayout dependency

- **libs.versions.toml** - Updated with:
  - ConstraintLayout version (2.2.0)
  - Library reference added

##### Documentation

- **README.md** - Main documentation

  - Feature overview
  - Project structure
  - Customization guide (package name, app name)
  - API configuration instructions
  - Utility function examples
  - Dependency list
  - Next steps guide

- **BUILD_GUIDE.md** - Build and configuration guide

  - Prerequisites
  - Build instructions (Android Studio & CLI)
  - Common build issues and solutions
  - Quick start guide
  - Project structure verification
  - Testing instructions
  - Customization checklist
  - Troubleshooting section

- **QUICK_REFERENCE.md** - API reference guide

  - Network request examples (GET, POST, PUT, DELETE)
  - All helper function examples with code
  - Common patterns (login, fetch data, logout)
  - Configuration examples
  - Best practices and tips

- **PROJECT_SUMMARY.md** - Complete project overview
  - Feature checklist
  - Component descriptions
  - Project structure visualization
  - Dependencies list
  - Key files description
  - Usage examples
  - Next steps recommendations

##### Scripts

- **setup.sh** - Automated customization script
  - Interactive prompts for package name and app name
  - Updates build.gradle.kts
  - Updates strings.xml
  - Updates URLS.kt configuration
  - Updates package declarations in Kotlin files
  - Updates imports across the project
  - Moves package directories
  - Cleans up old directories
  - Executable permissions set

### 🔧 Technical Details

#### Minimum Requirements

- minSdk: 33 (Android 13)
- targetSdk: 36
- compileSdk: 36
- Java: 11
- Kotlin: 2.0.21

#### Dependencies

- AndroidX Core KTX: 1.17.0
- AndroidX AppCompat: 1.7.1
- Material Components: 1.13.0 & 1.11.0
- ConstraintLayout: 2.2.0
- Volley: 1.2.1

#### Package Structure

```
ug.global.temp/
├── MainActivity
└── util/
    ├── Helpers
    ├── URLS
    └── VolleyHelper
```

### 🎨 UI/UX Features

- Material Design 3 implementation
- Bottom sheet modal for login
- Responsive layouts with ConstraintLayout
- Loading states with progress indicators
- Error feedback with Snackbar and Toast
- Confirmation dialogs for critical actions
- Password visibility toggle
- Form validation with real-time feedback
- Smooth transitions between states

### 🔐 Security Features

- Secure token storage in SharedPreferences
- Password input masking
- Network security configuration
- Session management with auto-logout
- Cleartext traffic control (development only)

### 📱 User Features

- Bottom sheet login with email/password
- Form validation (email format, password length)
- Remember login state
- Logout with confirmation
- Network availability checking
- User-friendly error messages
- Loading indicators during operations

### ✨ Developer Features

- 50+ utility helper functions
- Simplified network request API
- Centralized URL management
- Environment switching capability
- ViewBinding enabled
- Comprehensive inline documentation
- Clean architecture structure
- Easy customization with setup script

### 🚀 Ready-to-Use Components

- Login flow (complete)
- Session management (complete)
- Network layer (complete)
- Error handling (complete)
- Input validation (complete)
- UI feedback system (complete)
- Date/time utilities (complete)
- SharedPreferences wrapper (complete)

---

## How to Use This Changelog

This changelog documents the initial creation of the Android Starter App.

When you customize this app for your project:

1. Run `./setup.sh` to change package and app names
2. Update this changelog with your app's version history
3. Track your features, fixes, and changes here

### Changelog Format

```
## [Version] - YYYY-MM-DD

### Added
- New features

### Changed
- Changes to existing functionality

### Fixed
- Bug fixes

### Removed
- Removed features
```

---

**Initial Development**: December 14, 2024
**Status**: Production Ready ✅
**License**: Free to use for any project

Happy Coding! 🚀
