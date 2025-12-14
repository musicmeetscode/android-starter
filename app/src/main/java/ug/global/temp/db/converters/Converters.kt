package ug.global.temp.db.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * TypeConverters for Room Database
 * 
 * These converters allow Room to store complex data types that aren't natively supported.
 * Add your custom converters here as needed.
 */
class Converters {
    
    /**
     * Convert timestamp to Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    /**
     * Convert Date to timestamp
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * Convert comma-separated string to List
     */
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.filter { it.isNotBlank() }
    }
    
    /**
     * Convert List to comma-separated string
     */
    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
    
    /**
     * Convert JSON string to Map (basic implementation)
     */
    @TypeConverter
    fun fromJsonString(value: String?): Map<String, String>? {
        if (value.isNullOrBlank()) return null
        
        return try {
            value.split(";")
                .filter { it.contains(":") }
                .associate {
                    val (key, v) = it.split(":", limit = 2)
                    key.trim() to v.trim()
                }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convert Map to JSON string (basic implementation)
     */
    @TypeConverter
    fun mapToJsonString(map: Map<String, String>?): String? {
        return map?.entries?.joinToString(";") { "${it.key}:${it.value}" }
    }
    
    /**
     * Convert Boolean to Int (for custom boolean storage)
     */
    @TypeConverter
    fun fromBoolean(value: Boolean?): Int? {
        return when (value) {
            true -> 1
            false -> 0
            null -> null
        }
    }
    
    /**
     * Convert Int to Boolean
     */
    @TypeConverter
    fun toBoolean(value: Int?): Boolean? {
        return when (value) {
            1 -> true
            0 -> false
            else -> null
        }
    }
}
