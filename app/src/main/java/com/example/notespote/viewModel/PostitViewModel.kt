// viewModel/PostitViewModel.kt
package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Postit
import com.example.notespote.domain.usecases.postits.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostitViewModel @Inject constructor(
    private val createPostitUseCase: CreatePostitUseCase,
    private val getPostitsByApunteUseCase: GetPostitsByApunteUseCase,
    private val updatePostitUseCase: UpdatePostitUseCase,
    private val deletePostitUseCase: DeletePostitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostitUiState>(PostitUiState.Idle)
    val uiState: StateFlow<PostitUiState> = _uiState.asStateFlow()

    private val _postits = MutableStateFlow<List<Postit>>(emptyList())
    val postits: StateFlow<List<Postit>> = _postits.asStateFlow()

    fun loadPostitsByApunte(apunteId: String) {
        viewModelScope.launch {
            _uiState.value = PostitUiState.Loading

            getPostitsByApunteUseCase(apunteId).collect { result ->
                result.onSuccess { postitsList ->
                    _postits.value = postitsList
                    _uiState.value = PostitUiState.Success
                }.onFailure { error ->
                    _uiState.value = PostitUiState.Error(error.message ?: "Error al cargar post-its")
                }
            }
        }
    }

    fun createPostit(
        idApunte: String,
        titulo: String?,
        contenido: String?,
        color: String,
        posicionX: Int,
        posicionY: Int,
        ancho: Int = 200,
        alto: Int = 200
    ) {
        viewModelScope.launch {
            _uiState.value = PostitUiState.Loading

            val result = createPostitUseCase(
                idApunte = idApunte,
                titulo = titulo,
                contenido = contenido,
                color = color,
                posicionX = posicionX,
                posicionY = posicionY,
                ancho = ancho,
                alto = alto
            )

            _uiState.value = if (result.isSuccess) {
                PostitUiState.Created(result.getOrNull()!!)
            } else {
                PostitUiState.Error(result.exceptionOrNull()?.message ?: "Error al crear post-it")
            }
        }
    }

    fun updatePostit(postit: Postit) {
        viewModelScope.launch {
            val result = updatePostitUseCase(postit)

            if (result.isFailure) {
                _uiState.value = PostitUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al actualizar post-it"
                )
            }
        }
    }

    fun deletePostit(postitId: String) {
        viewModelScope.launch {
            _uiState.value = PostitUiState.Loading

            val result = deletePostitUseCase(postitId)

            _uiState.value = if (result.isSuccess) {
                PostitUiState.Deleted
            } else {
                PostitUiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar post-it")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = PostitUiState.Idle
    }
}

sealed class PostitUiState {
    object Idle : PostitUiState()
    object Loading : PostitUiState()
    object Success : PostitUiState()
    data class Created(val postitId: String) : PostitUiState()
    object Deleted : PostitUiState()
    data class Error(val message: String) : PostitUiState()
}