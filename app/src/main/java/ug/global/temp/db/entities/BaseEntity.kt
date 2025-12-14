package ug.global.temp.db.entities

import java.util.Date

/**
 * Base Entity Interface
 * 
 * Implement this interface in your entities to get standard fields
 * like id, createdAt, updatedAt, and softDelete support.
 */
interface BaseEntity {
    val id: Long
    val createdAt: Date
    val updatedAt: Date
    val isDeleted: Boolean
}
