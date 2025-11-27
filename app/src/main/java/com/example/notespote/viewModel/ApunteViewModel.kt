package com.example.notespote.viewModel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ApunteViewModel @Inject constructor(
    private val apunteRepository: ApunteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApunteUiState>(ApunteUiState.Loading)
    val uiState: StateFlow<ApunteUiState> = _uiState.asStateFlow()

    fun loadApuntes(userId: String) {
        viewModelScope.launch {
            apunteRepository.getApuntesByUser(userId)
                .collectLatest { result ->
                    _uiState.value = when {
                        result.isSuccess -> ApunteUiState.Success(result.getOrNull()!!)
                        else -> ApunteUiState.Error(result.exceptionOrNull()?.message)
                    }
                }
        }
    }

    fun createApunte(apunte: com.example.notespote.domain.model.Apunte, archivos: List<Uri>) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading
            val result = apunteRepository.createApunte(apunte, archivos)
            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Created(result.getOrNull()!!)
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message)
            }
        }
    }

    fun deleteApunte(id: String) {
        viewModelScope.launch {
            apunteRepository.deleteApunte(id)
        }
    }
}

sealed class ApunteUiState {
    object Loading : ApunteUiState()
    data class Success(val apuntes: List<com.example.notespote.domain.model.Apunte>) : ApunteUiState()
    data class Created(val apunteId: String) : ApunteUiState()
    data class Error(val message: String?) : ApunteUiState()
}