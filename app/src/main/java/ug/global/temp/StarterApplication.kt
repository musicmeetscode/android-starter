package ug.global.temp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * StarterApplication - Main Application class
 * 
 * @HiltAndroidApp triggers Hilt code generation
 * This class initializes libraries and app-wide configurations
 */
@HiltAndroidApp
class StarterApplication : Application(), ImageLoaderFactory {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        initializeTimber()
        
        Timber.d("Application started - Build Type: ${BuildConfig.BUILD_TYPE}")
        Timber.d("Base URL: ${BuildConfig.BASE_URL}")
    }
    
    /**
     * Initialize Timber logging
     */
    private fun initializeTimber() {
        if (BuildConfig.DEBUG_MODE) {
            // Debug: Plant debug tree
            Timber.plant(Timber.DebugTree())
        } else {
            // Production: Plant custom tree for crash reporting
            Timber.plant(ProductionTree())
        }
    }
    
    /**
     * Configure Coil Image Loader
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .apply {
                if (BuildConfig.DEBUG_MODE) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
    
    /**
     * Production Timber Tree - Send logs to crash reporting
     */
    private class ProductionTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // In production, send error logs to your crash reporting service
            // e.g., Firebase Crashlytics:
            // if (priority == Log.ERROR || priority == Log.WARN) {
            //     FirebaseCrashlytics.getInstance().log(message)
            //     t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
            // }
        }
    }
}
