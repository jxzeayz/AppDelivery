package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.data.local.user.UserEntity

data class ProfileUiState(
    val user: UserEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()
    
    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val user = repository.getUserById(userId)
                _state.value = _state.value.copy(
                    user = user,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar perfil"
                )
            }
        }
    }
    
    fun updateProfile(userId: Long, name: String, phone: String, address: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, success = false)
            try {
                val result = repository.updateProfile(userId, name, phone, address)
                if (result.isSuccess) {
                    loadUser(userId) // Recargar datos actualizados
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Error al actualizar perfil"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al actualizar perfil"
                )
            }
        }
    }
    
    fun clearSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}
