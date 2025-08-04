package com.example.trustie.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trustie.data.GlobalStateManager
import com.example.trustie.data.model.datamodel.User
import com.example.trustie.repository.authrepo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val globalStateManager: GlobalStateManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val user = globalStateManager.currentUser.value
                if (user != null) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Failed to load profile: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                globalStateManager.clearUser()
                _profileState.value = ProfileState.LoggedOut
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Logout failed: ${e.message}")
            }
        }
    }

    fun getUserName(): String {
        return globalStateManager.getUserName() ?: "Unknown User"
    }

    fun isUserElderly(): Boolean {
        return globalStateManager.isUserElderly()
    }

    fun getUserId(): Int? {
        return globalStateManager.getUserId()
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
    object LoggedOut : ProfileState()
} 