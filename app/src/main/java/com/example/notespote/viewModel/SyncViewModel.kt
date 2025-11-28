// viewModel/SyncViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.EstadoSincronizacion
import com.example.notespote.domain.usecases.sync.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncAllUseCase: SyncAllUseCase,
    private val getEstadoSincronizacionUseCase: GetEstadoSincronizacionUseCase,
    private val getPendingSyncCountUseCase: GetPendingSyncCountUseCase,
    private val clearSyncErrorsUseCase: ClearSyncErrorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    private val _estadoSincronizacion = MutableStateFlow(EstadoSincronizacion())
    val estadoSincronizacion: StateFlow<EstadoSincronizacion> = _estadoSincronizacion.asStateFlow()

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    init {
        observeEstadoSincronizacion()
        loadPendingCount()
    }

    private fun observeEstadoSincronizacion() {
        viewModelScope.launch {
            getEstadoSincronizacionUseCase().collect { estado ->
                _estadoSincronizacion.value = estado
            }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            _uiState.value = SyncUiState.Syncing

            val result = syncAllUseCase()

            _uiState.value = if (result.isSuccess) {
                loadPendingCount()
                SyncUiState.Success
            } else {
                SyncUiState.Error(result.exceptionOrNull()?.message ?: "Error al sincronizar")
            }
        }
    }

    fun loadPendingCount() {
        viewModelScope.launch {
            val result = getPendingSyncCountUseCase()

            result.onSuccess { count ->
                _pendingCount.value = count
            }
        }
    }

    fun clearErrors() {
        viewModelScope.launch {
            clearSyncErrorsUseCase()
        }
    }

    fun resetUiState() {
        _uiState.value = SyncUiState.Idle
    }
}

sealed class SyncUiState {
    object Idle : SyncUiState()
    object Syncing : SyncUiState()
    object Success : SyncUiState()
    data class Error(val message: String) : SyncUiState()
}