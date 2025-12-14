package ug.global.temp.util

/**
 * Resource - A generic class that holds a value with its loading status.
 * Used for data flow from Repository to ViewModel.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

/**
 * UiState - Represents the state of a specific screen/view.
 * Used for data flow from ViewModel to UI (Activity/Fragment).
 * 
 * Usage:
 * val uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
