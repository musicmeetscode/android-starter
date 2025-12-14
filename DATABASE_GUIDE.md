# Room Database Guide

Complete guide to using the Room database in this starter app.

## Table of Contents

- [Overview](#overview)
- [Database Structure](#database-structure)
- [Quick Start](#quick-start)
- [Creating Entities](#creating-entities)
- [Creating DAOs](#creating-daos)
- [Using Repositories](#using-repositories)
- [Database Operations](#database-operations)
- [Migrations](#migrations)
- [Best Practices](#best-practices)

---

## Overview

This app uses **Room** - Google's recommended database solution for Android, which provides:

- ✅ Compile-time verification of SQL queries
- ✅ Minimized boilerplate code
- ✅ Database migration support
- ✅ Integration with LiveData and Flow for reactive updates
- ✅ Kotlin Coroutines support

### Architecture

```
UI Layer (Activity/Fragment)
     ↓
Repository Layer (Business Logic)
     ↓
DAO Layer (Database Access)
     ↓
Room Database (SQLite)
```

---

## Database Structure

```
app/src/main/java/ug/global/temp/db/
├── entities/           # Data models (@Entity classes)
│   ├── BaseEntity.kt   # Interface for common fields
│   └── User.kt         # Example user entity
│
├── dao/               # Data Access Objects (@Dao interfaces)
│   ├── BaseDao.kt     # Generic DAO with CRUD operations
│   └── UserDao.kt     # Example user DAO
│
├── repository/        # Repository pattern implementation
│   ├── BaseRepository.kt  # Generic repository
│   └── UserRepository.kt  # Example user repository
│
├── database/          # Database configuration
│   ├── AppDatabase.kt     # Main database class
│   └── DatabaseHelper.kt  # Utility functions
│
└── converters/        # Type converters
    └── Converters.kt  # Convert complex types
```

---

## Quick Start

### 1. Get Repository Instance

```kotlin
// In your Activity or Fragment
val userRepository = Helpers.getUserRepository(this)
```

### 2. Insert Data

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Launch coroutine
CoroutineScope(Dispatchers.IO).launch {
    val user = User(
        email = "john@example.com",
        username = "johndoe",
        fullName = "John Doe"
    )

    val userId = userRepository.insert(user)

    // Update UI on main thread
    withContext(Dispatchers.Main) {
        Helpers.showToast(this@YourActivity, "User created with ID: $userId")
    }
}
```

### 3. Query Data with Flow (Reactive)

```kotlin
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// Collect Flow in a lifecycle-aware scope
lifecycleScope.launch {
    userRepository.getAllUsers().collect { users ->
        // UI updates automatically when data changes
        updateUI(users)
    }
}
```

### 4. Query Data Once

```kotlin
lifecycleScope.launch {
    val user = userRepository.getUserByEmail("john@example.com")
    if (user != null) {
        // Use user data
        binding.tvName.text = user.fullName
    }
}
```

---

## Creating Entities

Entities are data classes that represent tables in your database.

### Example: Creating a Product Entity

```kotlin
package ug.global.temp.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["category"]),
        Index(value = ["isDeleted"])
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    override val id: Long = 0,

    val name: String,
    val sku: String,
    val description: String? = null,
    val price: Double,
    val category: String,
    val quantity: Int = 0,
    val imageUrl: String? = null,

    // Timestamps
    override val createdAt: Date = Date(),
    override val updatedAt: Date = Date(),

    // Soft delete
    override val isDeleted: Boolean = false
) : BaseEntity
```

### Entity Best Practices

1. **Index frequently queried fields** for faster searches
2. **Use nullable types** (`?`) for optional fields
3. **Provide default values** where appropriate
4. **Implement BaseEntity** for standard fields
5. **Use descriptive table names** (plural form)

---

## Creating DAOs

DAOs define methods to access the database.

### Example: Creating a Product DAO

```kotlin
package ug.global.temp.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ug.global.temp.db.entities.Product

@Dao
interface ProductDao : BaseDao<Product> {

    // Get all products
    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Get product by ID
    @Query("SELECT * FROM products WHERE id = :productId AND isDeleted = 0")
    suspend fun getProductById(productId: Long): Product?

    // Search products by name
    @Query("""
        SELECT * FROM products
        WHERE name LIKE '%' || :query || '%'
        AND isDeleted = 0
        ORDER BY name ASC
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    // Get products by category
    @Query("SELECT * FROM products WHERE category = :category AND isDeleted = 0")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    // Update quantity
    @Query("UPDATE products SET quantity = :quantity, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateQuantity(productId: Long, quantity: Int, timestamp: Long = System.currentTimeMillis()): Int

    // Soft delete
    @Query("UPDATE products SET isDeleted = 1, updatedAt = :timestamp WHERE id = :productId")
    suspend fun softDelete(productId: Long, timestamp: Long = System.currentTimeMillis()): Int
}
```

### DAO Query Types

| Annotation    | Return Type | Use Case                       |
| ------------- | ----------- | ------------------------------ |
| `Flow<T>`     | Reactive    | Auto-updates when data changes |
| `suspend fun` | One-time    | Single fetch with coroutines   |
| `List<T>`     | Synchronous | Avoid - blocks thread          |

---

## Using Repositories

Repositories provide a clean API for data operations and business logic.

### Example: Creating a Product Repository

```kotlin
package ug.global.temp.db.repository

import kotlinx.coroutines.flow.Flow
import ug.global.temp.db.dao.ProductDao
import ug.global.temp.db.entities.Product

class ProductRepository(
    private val productDao: ProductDao
) : BaseRepository<Product>(productDao) {

    // Expose DAO methods
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    // Business logic methods
    suspend fun addProduct(
        name: String,
        sku: String,
        price: Double,
        category: String
    ): Result<Product> {
        return try {
            val product = Product(
                name = name,
                sku = sku,
                price = price,
                category = category
            )

            val id = insert(product)
            val savedProduct = getProductById(id)

            if (savedProduct != null) {
                Result.success(savedProduct)
            } else {
                Result.failure(Exception("Failed to save product"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStock(productId: Long, quantity: Int): Result<Boolean> {
        return try {
            val result = productDao.updateQuantity(productId, quantity)
            Result.success(result > 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Repository Best Practices

1. **Wrap operations in try-catch** and return `Result<T>`
2. **Add validation logic** before database operations
3. **Combine multiple DAO operations** in single method
4. **Transform data** if needed before returning to UI

---

## Database Operations

### Insert Operations

```kotlin
// Single insert
val id = repository.insert(entity)

// Multiple insert
val ids = repository.insertAll(entity1, entity2, entity3)

// Insert list
val ids = repository.insertList(listOf(entity1, entity2))

// Upsert (insert or update)
val id = repository.upsert(entity)
```

### Update Operations

```kotlin
// Update entity
val rowsUpdated = repository.update(entity)

// Update specific fields
val updated = dao.updateQuantity(productId, newQuantity)
```

### Delete Operations

```kotlin
// Soft delete (recommended)
dao.softDeleteUser(userId)

// Hard delete
repository.delete(entity)

// Delete all
dao.deleteAllUsers()
```

### Query Operations

```kotlin
// Reactive query (Flow)
repository.getAllProducts().collect { products ->
    // Updates automatically
}

// One-time query
val user = repository.getUserById(123)

// Search query
repository.searchProducts("laptop").collect { results ->
    // Display search results
}
```

---

## Migrations

When you change your database schema, you need to create migrations.

### Step 1: Modify Entity

```kotlin
// Add new field to Product entity
@Entity(tableName = "products")
data class Product(
    // ... existing fields ...
    val manufacturer: String = "" // NEW FIELD
)
```

### Step 2: Increment Database Version

In `AppDatabase.kt`:

```kotlin
@Database(
    entities = [User::class, Product::class],
    version = 2, // Changed from 1 to 2
    exportSchema = true
)
```

### Step 3: Create Migration

In `AppDatabase.kt` companion object:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE products ADD COLUMN manufacturer TEXT NOT NULL DEFAULT ''"
        )
    }
}
```

### Step 4: Add Migration to Database Builder

```kotlin
private fun buildDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(...)
        .addMigrations(MIGRATION_1_2)
        // Remove or comment out for production:
        // .fallbackToDestructiveMigration()
        .build()
}
```

### Migration Examples

**Add Column:**

```sql
ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0
```

**Create Table:**

```sql
CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    userId INTEGER NOT NULL,
    total REAL NOT NULL,
    FOREIGN KEY(userId) REFERENCES users(id)
)
```

**Create Index:**

```sql
CREATE INDEX IF NOT EXISTS index_orders_userId ON orders(userId)
```

---

## Best Practices

### ✅ DO

1. **Use suspend functions** for database operations
2. **Use Flow** for reactive data that updates automatically
3. **Add indices** to frequently queried columns
4. **Implement soft delete** instead of hard delete
5. **Add timestamps** (createdAt, updatedAt) to all entities
6. **Use transactions** for multiple related operations
7. **Handle errors** with try-catch and Result<T>
8. **Create migrations** for schema changes in production

### ❌ DON'T

1. **Don't perform database operations on main thread**
2. **Don't use LiveData** - use Flow instead (more powerful)
3. **Don't ignore TypeConverters** for complex types
4. **Don't forget to close database** in tests
5. **Don't use destructive migration** in production
6. **Don't expose DAOs directly** - use repositories

### Performance Tips

1. **Batch inserts** - Use `insertList()` instead of looping `insert()`
2. **Limit results** - Add `LIMIT` clause for large datasets
3. **Use indices** - On columns used in WHERE, ORDER BY, JOIN
4. **Avoid N+1 queries** - Use JOIN instead of separate queries
5. **Use transactions** - Wrap multiple operations for atomicity

### Testing

```kotlin
// In your test class
@Before
fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inDatabaseBuilder(
        context,
        AppDatabase::class.java,
        "test_database"
    ).build()
    userDao = database.userDao()
}

@After
fun tearDown() {
    database.close()
}

@Test
fun insertAndRetrieveUser() = runBlocking {
    val user = User(email = "test@test.com", username = "test")
    val id = userDao.insert(user)

    val retrieved = userDao.getUserByIdOnce(id)
    assertEquals(user.email, retrieved?.email)
}
```

---

## Common Patterns

### Save User After Login

```kotlin
lifecycleScope.launch {
    val result = userRepository.registerUser(
        email = "user@example.com",
        username = "username",
        fullName = "Full Name"
    )

    result.onSuccess { user ->
        Helpers.saveString(this@Activity, "user_id", user.id.toString())
        Helpers.showToast(this@Activity, "Welcome!")
    }.onFailure { error ->
        Helpers.showToast(this@Activity, error.message ?: "Error")
    }
}
```

### Sync with Network

```kotlin
suspend fun syncUsers() {
    // Fetch from network
    VolleyHelper.get(context, URLS.GET_USERS, { response ->
        lifecycleScope.launch {
            // Parse and save to database
            val users = parseUsers(response)
            userRepository.insertList(users)
        }
    }, { error ->
        // Handle error
    })
}
```

### Search with Debounce

```kotlin
searchEditText.addTextChangedListener { text ->
    lifecycleScope.launch {
        delay(300) // Debounce
        userRepository.searchUsers(text.toString()).collect { results ->
            adapter.submitList(results)
        }
    }
}
```

---

## Troubleshooting

### "Cannot access database on the main thread"

**Solution:** Use `suspend` functions or coroutines.

### "Migration didn't properly handle..."

**Solution:** Write proper migration or use `.fallbackToDestructiveMigration()` for development.

### "Entity has some fields but they are not returned/used..."

**Solution:** Make sure your query returns all entity fields.

### "Flow doesn't update when data changes"

**Solution:** Ensure you're using `@Query` that returns `Flow` and collecting it properly.

---

## Additional Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Kotlin Flow Guide](https://developer.android.com/kotlin/flow)
- [Database Inspector](https://developer.android.com/studio/inspect/database) - Debug in AndroidStudio

---

**You're now ready to use Room database in your app!** 🚀

For questions, check the example implementations in `User` entity, `UserDao`, and `UserRepository`.
