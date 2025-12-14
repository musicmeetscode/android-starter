package ug.global.temp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ug.global.temp.util.UiState

/**
 * BaseViewModel - Common functionality for all ViewModels
 * 
 * Includes:
 * - Error handling
 * - UI State helper methods
 * - Coroutine launching with error catching
 */
abstract class BaseViewModel : ViewModel() {
    
    // Generic error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Generic loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Exception Handler
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }
    
    protected open fun onError(throwable: Throwable) {
        _error.value = throwable.message ?: "Unknown error occurred"
        _isLoading.value = false
        throwable.printStackTrace()
    }
    
    /**
     * Launch a coroutine with error handling
     */
    protected fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _isLoading.value = true
            try {
                block()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Helper to update UI State
     */
    protected fun <T> MutableStateFlow<UiState<T>>.setSuccess(data: T) {
        value = UiState.Success(data)
    }
    
    protected fun <T> MutableStateFlow<UiState<T>>.setError(message: String) {
        value = UiState.Error(message)
    }
    
    protected fun <T> MutableStateFlow<UiState<T>>.setLoading() {
        value = UiState.Loading
    }
}
