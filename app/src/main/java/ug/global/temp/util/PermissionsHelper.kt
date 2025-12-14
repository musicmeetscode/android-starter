package ug.global.temp.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * PermissionsHelper - Utility for handling runtime permissions
 */
object PermissionsHelper {
    
    /**
     * Check if a permission is granted
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check multiple permissions
     */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { hasPermission(context, it) }
    }
    
    /**
     * Request permission using new ActivityResult API (Launcher)
     * 
     * Usage in Activity/Fragment:
     * val requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> ... }
     * PermissionsHelper.requestPermission(requestLauncher, Manifest.permission.CAMERA)
     */
    fun requestPermission(
        launcher: ActivityResultLauncher<String>,
        permission: String
    ) {
        launcher.launch(permission)
    }
    
    /**
     * Request multiple permissions
     */
    fun requestPermissions(
        launcher: ActivityResultLauncher<Array<String>>,
        permissions: Array<String>
    ) {
        launcher.launch(permissions)
    }
    
    /**
     * Notification Permission (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }
}
