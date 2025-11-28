// viewModel/CarpetaViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.CarpetaContenido
import com.example.notespote.domain.usecases.folders.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarpetaViewModel @Inject constructor(
    private val createCarpetaUseCase: CreateCarpetaUseCase,
    private val getCarpetasRaizUseCase: GetCarpetasRaizUseCase,
    private val getSubcarpetasUseCase: GetSubcarpetasUseCase,
    private val getCarpetaConContenidoUseCase: GetCarpetaConContenidoUseCase,
    private val updateCarpetaUseCase: UpdateCarpetaUseCase,
    private val deleteCarpetaUseCase: DeleteCarpetaUseCase,
    private val moverCarpetaUseCase: MoverCarpetaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CarpetaUiState>(CarpetaUiState.Idle)
    val uiState: StateFlow<CarpetaUiState> = _uiState.asStateFlow()

    private val _carpetas = MutableStateFlow<List<Carpeta>>(emptyList())
    val carpetas: StateFlow<List<Carpeta>> = _carpetas.asStateFlow()

    private val _carpetaContenido = MutableStateFlow<CarpetaContenido?>(null)
    val carpetaContenido: StateFlow<CarpetaContenido?> = _carpetaContenido.asStateFlow()

    fun loadCarpetasRaiz(userId: String) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            getCarpetasRaizUseCase(userId).collect { result ->
                result.onSuccess { carpetasList ->
                    _carpetas.value = carpetasList
                    _uiState.value = CarpetaUiState.Success
                }.onFailure { error ->
                    _uiState.value = CarpetaUiState.Error(error.message ?: "Error al cargar carpetas")
                }
            }
        }
    }

    fun loadSubcarpetas(carpetaPadreId: String) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            getSubcarpetasUseCase(carpetaPadreId).collect { result ->
                result.onSuccess { carpetasList ->
                    _carpetas.value = carpetasList
                    _uiState.value = CarpetaUiState.Success
                }.onFailure { error ->
                    _uiState.value = CarpetaUiState.Error(error.message ?: "Error al cargar subcarpetas")
                }
            }
        }
    }

    fun loadCarpetaConContenido(carpetaId: String) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            getCarpetaConContenidoUseCase(carpetaId).collect { result ->
                result.onSuccess { contenido ->
                    _carpetaContenido.value = contenido
                    _uiState.value = CarpetaUiState.Success
                }.onFailure { error ->
                    _uiState.value = CarpetaUiState.Error(error.message ?: "Error al cargar carpeta")
                }
            }
        }
    }

    fun createCarpeta(
        nombreCarpeta: String,
        colorCarpeta: String?,
        descripcion: String?,
        idCarpetaPadre: String?,
        idMateria: String?
    ) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            val result = createCarpetaUseCase(
                nombreCarpeta = nombreCarpeta,
                colorCarpeta = colorCarpeta,
                descripcion = descripcion,
                idCarpetaPadre = idCarpetaPadre,
                idMateria = idMateria
            )

            _uiState.value = if (result.isSuccess) {
                CarpetaUiState.Created(result.getOrNull()!!)
            } else {
                CarpetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al crear carpeta")
            }
        }
    }

    fun updateCarpeta(carpeta: Carpeta) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            val result = updateCarpetaUseCase(carpeta)

            _uiState.value = if (result.isSuccess) {
                CarpetaUiState.Updated
            } else {
                CarpetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar carpeta")
            }
        }
    }

    fun deleteCarpeta(carpetaId: String) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            val result = deleteCarpetaUseCase(carpetaId)

            _uiState.value = if (result.isSuccess) {
                CarpetaUiState.Deleted
            } else {
                CarpetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar carpeta")
            }
        }
    }

    fun moverCarpeta(carpetaId: String, nuevoPadreId: String?) {
        viewModelScope.launch {
            _uiState.value = CarpetaUiState.Loading

            val result = moverCarpetaUseCase(carpetaId, nuevoPadreId)

            _uiState.value = if (result.isSuccess) {
                CarpetaUiState.Moved
            } else {
                CarpetaUiState.Error(result.exceptionOrNull()?.message ?: "Error al mover carpeta")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = CarpetaUiState.Idle
    }
}

sealed class CarpetaUiState {
    object Idle : CarpetaUiState()
    object Loading : CarpetaUiState()
    object Success : CarpetaUiState()
    data class Created(val carpetaId: String) : CarpetaUiState()
    object Updated : CarpetaUiState()
    object Deleted : CarpetaUiState()
    object Moved : CarpetaUiState()
    data class Error(val message: String) : CarpetaUiState()
}