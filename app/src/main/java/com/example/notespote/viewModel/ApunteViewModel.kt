// viewModel/ApunteViewModel.kt
package com.example.notespote.viewModel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.usecases.notes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApunteViewModel @Inject constructor(
    private val createApunteUseCase: CreateApunteUseCase,
    private val getApuntesByUserUseCase: GetApuntesByUserUseCase,
    private val getApuntesByFolderUseCase: GetApuntesByFolderUseCase,
    private val getApunteByIdUseCase: GetApunteByIdUseCase,
    private val updateApunteUseCase: UpdateApunteUseCase,
    private val deleteApunteUseCase: DeleteApunteUseCase,
    private val guardarApunteUseCase: GuardarApunteUseCase,
    private val getPublicApuntesUseCase: GetPublicApuntesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApunteUiState>(ApunteUiState.Idle)
    val uiState: StateFlow<ApunteUiState> = _uiState.asStateFlow()

    private val _apuntes = MutableStateFlow<List<Apunte>>(emptyList())
    val apuntes: StateFlow<List<Apunte>> = _apuntes.asStateFlow()

    private val _apunteDetallado = MutableStateFlow<ApunteDetallado?>(null)
    val apunteDetallado: StateFlow<ApunteDetallado?> = _apunteDetallado.asStateFlow()

    fun loadApuntesByUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            getApuntesByUserUseCase(userId).collect { result ->
                result.onSuccess { apuntesList ->
                    _apuntes.value = apuntesList
                    _uiState.value = ApunteUiState.Success
                }.onFailure { error ->
                    _uiState.value = ApunteUiState.Error(error.message ?: "Error al cargar apuntes")
                }
            }
        }
    }

    fun loadApuntesByFolder(folderId: String) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            getApuntesByFolderUseCase(folderId).collect { result ->
                result.onSuccess { apuntesList ->
                    _apuntes.value = apuntesList
                    _uiState.value = ApunteUiState.Success
                }.onFailure { error ->
                    _uiState.value = ApunteUiState.Error(error.message ?: "Error al cargar apuntes")
                }
            }
        }
    }

    fun loadApunteById(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            getApunteByIdUseCase(apunteId).collect { result ->
                result.onSuccess { apunteDetalle ->
                    _apunteDetallado.value = apunteDetalle
                    _uiState.value = ApunteUiState.Success
                }.onFailure { error ->
                    _uiState.value = ApunteUiState.Error(error.message ?: "Error al cargar apunte")
                }
            }
        }
    }

    fun loadPublicApuntes(limit: Int = 50) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            getPublicApuntesUseCase(limit).collect { result ->
                result.onSuccess { apuntesList ->
                    _apuntes.value = apuntesList
                    _uiState.value = ApunteUiState.Success
                }.onFailure { error ->
                    _uiState.value = ApunteUiState.Error(error.message ?: "Error al cargar apuntes públicos")
                }
            }
        }
    }

    fun createApunte(
        titulo: String,
        contenido: String?,
        idCarpeta: String?,
        idMateria: String?,
        tipoVisibilidad: TipoVisibilidad,
        archivos: List<Uri>
    ) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            val result = createApunteUseCase(
                titulo = titulo,
                contenido = contenido,
                idCarpeta = idCarpeta,
                idMateria = idMateria,
                tipoVisibilidad = tipoVisibilidad,
                archivos = archivos
            )

            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Created(result.getOrNull()!!)
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message ?: "Error al crear apunte")
            }
        }
    }

    fun updateApunte(apunte: Apunte) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            val result = updateApunteUseCase(apunte)

            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Updated
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar apunte")
            }
        }
    }

    fun deleteApunte(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            val result = deleteApunteUseCase(apunteId)

            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Deleted
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar apunte")
            }
        }
    }

    fun guardarApunte(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading

            val result = guardarApunteUseCase(apunteId)

            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Saved
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message ?: "Error al guardar apunte")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ApunteUiState.Idle
    }
}

sealed class ApunteUiState {
    object Idle : ApunteUiState()
    object Loading : ApunteUiState()
    object Success : ApunteUiState()
    data class Created(val apunteId: String) : ApunteUiState()
    object Updated : ApunteUiState()
    object Deleted : ApunteUiState()
    object Saved : ApunteUiState()
    data class Error(val message: String) : ApunteUiState()
}