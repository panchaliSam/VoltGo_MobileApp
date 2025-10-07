package lk.voltgo.voltgo.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.remote.dto.UpdateProfileRequest
import lk.voltgo.voltgo.data.remote.dto.UserProfileResponse
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<UserProfileResponse>>(UiState.Loading)
    val profileState = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<UiState<Unit>?>(null)
    val updateState = _updateState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            val result = authManager.getUserProfile()
            result
                .onSuccess { profile ->
                    _profileState.value = UiState.Success(profile)
                }
                .onFailure { e ->
                    _profileState.value = UiState.Error(e.message ?: "Failed to load profile")
                }
        }
    }

    fun updateProfile(updateProfileRequest: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            val result = authManager.updateProfile(updateProfileRequest)
            result
                .onSuccess {
                    _updateState.value = UiState.Success(Unit)
                    // optionally refresh:
                    // loadProfile()
                }
                .onFailure { e ->
                    _updateState.value = UiState.Error(e.message ?: "Update failed")
                }
        }
    }
}
