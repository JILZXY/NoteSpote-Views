// viewModel/ApunteViewModel.kt
package com.example.notespote.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.model.NoteBlock
import com.example.notespote.domain.usecases.etiquetas.AgregarEtiquetaAApunteUseCase
import com.example.notespote.domain.usecases.etiquetas.RemoverEtiquetaDeApunteUseCase
import com.example.notespote.domain.usecases.notes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ApunteViewModel @Inject constructor(
    private val createApunteUseCase: CreateApunteUseCase,
    private val getApuntesByUserUseCase: GetApuntesByUserUseCase,
    private val getApuntesByFolderUseCase: GetApuntesByFolderUseCase,
    private val getApunteByIdUseCase: GetApunteByIdUseCase,
    private val updateApunteUseCase: UpdateApunteUseCase,
    private val deleteApunteUseCase: DeleteApunteUseCase,
    private val guardarApunteUseCase: GuardarApunteUseCase,
    private val getPublicApuntesUseCase: GetPublicApuntesUseCase,
    private val agregarEtiquetaAApunteUseCase: AgregarEtiquetaAApunteUseCase,
    private val removerEtiquetaDeApunteUseCase: RemoverEtiquetaDeApunteUseCase,
    private val addArchivosToApunteUseCase: com.example.notespote.domain.usecases.archivos.AddArchivosToApunteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApunteUiState>(ApunteUiState.Idle)
    val uiState: StateFlow<ApunteUiState> = _uiState.asStateFlow()

    private val _apuntes = MutableStateFlow<List<Apunte>>(emptyList())
    val apuntes: StateFlow<List<Apunte>> = _apuntes.asStateFlow()

    private val apunteId = MutableStateFlow<String?>(null)

    // Gestión de bloques de contenido
    private val _noteBlocks = MutableStateFlow<List<NoteBlock>>(emptyList())
    val noteBlocks: StateFlow<List<NoteBlock>> = _noteBlocks.asStateFlow()

    val apunteDetallado: StateFlow<ApunteDetallado?> = apunteId.flatMapLatest { id ->
        Log.d("ApunteViewModel", "flatMapLatest triggered for id: $id")
        getApunteByIdUseCase(id!!).map { result ->
            result.onSuccess { detalle ->
                Log.d("ApunteViewModel", "Flow emitted new data with tags: ${detalle.etiquetas.map { it.nombreEtiqueta }}")
            }
            result.getOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setApunteId(id: String) {
        apunteId.value = id
        loadNoteBlocks()
    }

    /**
     * Convierte el contenido del apunte y archivos en bloques
     */
    private fun loadNoteBlocks() {
        viewModelScope.launch {
            apunteDetallado.collect { detalle ->
                if (detalle != null) {
                    val blocks = mutableListOf<NoteBlock>()
                    var order = 0

                    // Agregar contenido de texto como primer bloque si existe
                    if (!detalle.apunte.contenido.isNullOrBlank()) {
                        blocks.add(NoteBlock.TextBlock(
                            content = detalle.apunte.contenido,
                            order = order++
                        ))
                    }

                    // Agregar archivos como bloques
                    detalle.archivos.forEach { archivo ->
                        when {
                            archivo.esImagen -> {
                                blocks.add(NoteBlock.ImageBlock(
                                    rutaLocal = archivo.rutaLocal ?: "",
                                    nombreArchivo = archivo.nombreArchivo,
                                    order = order++
                                ))
                            }
                            archivo.esPDF -> {
                                blocks.add(NoteBlock.PdfBlock(
                                    rutaLocal = archivo.rutaLocal ?: "",
                                    nombreArchivo = archivo.nombreArchivo,
                                    tamanoKb = archivo.tamanoKb,
                                    order = order++
                                ))
                            }
                        }
                    }

                    _noteBlocks.value = blocks.sortedBy { it.order }
                }
            }
        }
    }

    /**
     * Agrega un nuevo bloque de texto
     */
    fun addTextBlock(content: String = "", position: Int? = null) {
        val currentBlocks = _noteBlocks.value.toMutableList()
        val order = position ?: currentBlocks.size
        val newBlock = NoteBlock.TextBlock(content = content, order = order)
        currentBlocks.add(newBlock)
        _noteBlocks.value = reorderBlocks(currentBlocks)
    }

    /**
     * Agrega un nuevo Post-it
     */
    fun addPostipBlock(content: String = "", colorHex: String = "#FFEB3B", position: Int? = null) {
        val currentBlocks = _noteBlocks.value.toMutableList()
        val order = position ?: currentBlocks.size
        val newBlock = NoteBlock.PostipBlock(content = content, colorHex = colorHex, order = order)
        currentBlocks.add(newBlock)
        _noteBlocks.value = reorderBlocks(currentBlocks)
    }

    /**
     * Actualiza el contenido de un bloque
     */
    fun updateBlock(blockId: String, newContent: String) {
        val currentBlocks = _noteBlocks.value.toMutableList()
        val index = currentBlocks.indexOfFirst { it.id == blockId }
        if (index != -1) {
            val block = currentBlocks[index]
            currentBlocks[index] = when (block) {
                is NoteBlock.TextBlock -> block.copy(content = newContent)
                is NoteBlock.PostipBlock -> block.copy(content = newContent)
                else -> block
            }
            _noteBlocks.value = currentBlocks
        }
    }

    /**
     * Elimina un bloque
     */
    fun deleteBlock(blockId: String) {
        val currentBlocks = _noteBlocks.value.toMutableList()
        currentBlocks.removeAll { it.id == blockId }
        _noteBlocks.value = reorderBlocks(currentBlocks)
    }

    /**
     * Mueve un bloque a una nueva posición
     */
    fun moveBlock(blockId: String, newPosition: Int) {
        val currentBlocks = _noteBlocks.value.toMutableList()
        val block = currentBlocks.find { it.id == blockId } ?: return
        currentBlocks.remove(block)

        val updatedBlock = when (block) {
            is NoteBlock.TextBlock -> block.copy(order = newPosition)
            is NoteBlock.ImageBlock -> block.copy(order = newPosition)
            is NoteBlock.PdfBlock -> block.copy(order = newPosition)
            is NoteBlock.PostipBlock -> block.copy(order = newPosition)
        }

        currentBlocks.add(newPosition.coerceIn(0, currentBlocks.size), updatedBlock)
        _noteBlocks.value = reorderBlocks(currentBlocks)
    }

    /**
     * Reordena los bloques para mantener índices consecutivos
     */
    private fun reorderBlocks(blocks: List<NoteBlock>): List<NoteBlock> {
        return blocks.mapIndexed { index, block ->
            when (block) {
                is NoteBlock.TextBlock -> block.copy(order = index)
                is NoteBlock.ImageBlock -> block.copy(order = index)
                is NoteBlock.PdfBlock -> block.copy(order = index)
                is NoteBlock.PostipBlock -> block.copy(order = index)
            }
        }.sortedBy { it.order }
    }

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
            Log.d("ApunteVM", "🎯 Iniciando creación...")
            _uiState.value = ApunteUiState.Loading
            val result = createApunteUseCase(
                titulo = titulo,
                contenido = contenido,
                idCarpeta = idCarpeta,
                idMateria = idMateria,
                tipoVisibilidad = tipoVisibilidad,
                archivos = archivos
            )
            Log.d("ApunteVM", "📊 Resultado: ${result.isSuccess}")
            _uiState.value = if (result.isSuccess) {
                Log.d("ApunteVM", "✅ Apunte creado: ${result.getOrNull()}")
                ApunteUiState.Created(result.getOrNull()!!)
            } else {
                Log.e("ApunteVM", "❌ Error: ${result.exceptionOrNull()?.message}")
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

    fun agregarEtiqueta(apunteId: String, nombreEtiqueta: String) {
        viewModelScope.launch {
            // The Flow will update automatically, no need to call loadApunteById
            agregarEtiquetaAApunteUseCase(apunteId, nombreEtiqueta)
        }
    }

    fun removerEtiqueta(apunteId: String, etiquetaId: String) {
        viewModelScope.launch {
            // The Flow will update automatically, no need to call loadApunteById
            removerEtiquetaDeApunteUseCase(apunteId, etiquetaId)
        }
    }

    fun addArchivos(apunteId: String, archivos: List<Uri>) {
        viewModelScope.launch {
            _uiState.value = ApunteUiState.Loading
            val result = addArchivosToApunteUseCase(apunteId, archivos)
            _uiState.value = if (result.isSuccess) {
                ApunteUiState.Success
            } else {
                ApunteUiState.Error(result.exceptionOrNull()?.message ?: "Error al agregar archivos")
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
