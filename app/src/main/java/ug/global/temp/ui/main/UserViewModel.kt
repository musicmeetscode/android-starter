package ug.global.temp.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ug.global.temp.db.entities.User
import ug.global.temp.db.repository.UserRepository
import ug.global.temp.ui.base.BaseViewModel
import ug.global.temp.util.UiState
import javax.inject.Inject

/**
 * UserViewModel - Example ViewModel
 * 
 * Demonstrates:
 * - Hilt Injection
 * - Flow/StateFlow
 * - Repository usage
 * - UI State management
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {
    
    // UI State for User List
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
    
    // UI State for Current User
    private val _currentUserState = MutableStateFlow<UiState<User?>>(UiState.Idle)
    val currentUserState: StateFlow<UiState<User?>> = _currentUserState.asStateFlow()
    
    init {
        // Automatically load users when ViewModel is created
        loadUsers()
    }
    
    /**
     * Load all users (Reactive)
     */
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.setLoading()
            try {
                userRepository.getAllUsers().collectLatest { users ->
                     if (users.isEmpty()) {
                         // Optional: Distinct empty state or just empty list
                         _usersState.setSuccess(emptyList()) 
                     } else {
                         _usersState.setSuccess(users)
                     }
                }
            } catch (e: Exception) {
                _usersState.setError(e.message ?: "Failed to load users")
            }
        }
    }
    
    /**
     * Register a new user
     */
    fun registerUser(email: String, username: String, fullName: String) {
        launchDataLoad {
            val result = userRepository.registerUser(email, username, fullName)
            
            result.onSuccess { user ->
                // Refresh or perform other actions
                // List will auto-update because of Flow
            }.onFailure { error ->
                onError(error) // Uses BaseViewModel's error handling
            }
        }
    }
    
    /**
     * Delete user
     */
    fun deleteUser(userId: Long) {
        launchDataLoad {
            userRepository.softDeleteUser(userId)
        }
    }
    
    /**
     * Simulate fetching from API and syncing to DB
     */
    fun syncUsers() {
        launchDataLoad {
            // 1. Fetch from API (e.g. via Retrofit)
            // val apiUsers = apiService.getUsers()
            
            // 2. Save to DB
            // userRepository.insertAll(apiUsers)
            
            // For now, just seed some dummy data
            ug.global.temp.db.database.DatabaseHelper.seedDatabase(
                // In a real app, don't pass context to ViewModel, stick to Repository/UseCase
                // This is just a quick example, usually Seed logic belongs in Repository
                // or a dedicated UseCase. We'll skip implementation here to avoid Context leak.
                // See DatabaseHelper.seedDatabase() call in Activity instead.
                throw NotImplementedError("Call seedDatabase from Activity/Repository")
            )
        }
    }
}
