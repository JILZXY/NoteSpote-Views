// viewModel/LikeViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.usecases.likes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val hasLikedUseCase: HasLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LikeUiState>(LikeUiState.Idle)
    val uiState: StateFlow<LikeUiState> = _uiState.asStateFlow()

    // Mapa de apunteId -> hasLiked
    private val _likedApuntes = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val likedApuntes: StateFlow<Map<String, Boolean>> = _likedApuntes.asStateFlow()

    // Mapa de apunteId -> likesCount
    private val _likesCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val likesCounts: StateFlow<Map<String, Int>> = _likesCounts.asStateFlow()

    fun checkIfLiked(apunteId: String) {
        viewModelScope.launch {
            hasLikedUseCase(apunteId).collect { result ->
                result.onSuccess { hasLiked ->
                    _likedApuntes.value = _likedApuntes.value.toMutableMap().apply {
                        put(apunteId, hasLiked)
                    }
                }
            }
        }
    }

    fun loadLikesCount(apunteId: String) {
        viewModelScope.launch {
            val result = getLikesCountUseCase(apunteId)

            result.onSuccess { count ->
                _likesCounts.value = _likesCounts.value.toMutableMap().apply {
                    put(apunteId, count)
                }
            }
        }
    }

    fun toggleLike(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = LikeUiState.Loading

            val result = toggleLikeUseCase(apunteId)

            _uiState.value = if (result.isSuccess) {
                // Actualizar estado local
                val currentLiked = _likedApuntes.value[apunteId] ?: false
                _likedApuntes.value = _likedApuntes.value.toMutableMap().apply {
                    put(apunteId, !currentLiked)
                }

                // Actualizar contador
                val currentCount = _likesCounts.value[apunteId] ?: 0
                val newCount = if (currentLiked) currentCount - 1 else currentCount + 1
                _likesCounts.value = _likesCounts.value.toMutableMap().apply {
                    put(apunteId, newCount)
                }

                LikeUiState.Success
            } else {
                LikeUiState.Error(result.exceptionOrNull()?.message ?: "Error al procesar like")
            }
        }
    }

    fun hasLiked(apunteId: String): Boolean {
        return _likedApuntes.value[apunteId] ?: false
    }

    fun getLikesCount(apunteId: String): Int {
        return _likesCounts.value[apunteId] ?: 0
    }

    fun resetUiState() {
        _uiState.value = LikeUiState.Idle
    }
}

sealed class LikeUiState {
    object Idle : LikeUiState()
    object Loading : LikeUiState()
    object Success : LikeUiState()
    data class Error(val message: String) : LikeUiState()
}