package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.usecases.auth.GetCurrentUserUseCase
import com.example.notespote.domain.usecases.folders.GetCarpetasRaizUseCase
import com.example.notespote.domain.usecases.notes.GetApuntesByUserUseCase
import com.example.notespote.domain.usecases.seguimiento.GetSeguidoresCountUseCase
import com.example.notespote.domain.usecases.seguimiento.GetSigueCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: Usuario? = null,
    val notes: List<Apunte> = emptyList(),
    val folders: List<Carpeta> = emptyList(),
    val followerCount: Long = 0,
    val followingCount: Long = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getApuntesByUserUseCase: GetApuntesByUserUseCase,
    private val getCarpetasRaizUseCase: GetCarpetasRaizUseCase,
    private val getSeguidoresCountUseCase: GetSeguidoresCountUseCase,
    private val getSigueCountUseCase: GetSigueCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val userResult = getCurrentUserUseCase().first()
                val user = userResult.getOrNull()

                if (user != null) {
                    val userId = user.id

                    val notesResult = getApuntesByUserUseCase(userId).first()
                    val foldersResult = getCarpetasRaizUseCase(userId).first()

                    val followersResult = getSeguidoresCountUseCase(userId)
                    val followingResult = getSigueCountUseCase(userId)

                    _uiState.value = _uiState.value.copy(
                        user = user,
                        notes = notesResult.getOrDefault(emptyList()),
                        folders = foldersResult.getOrDefault(emptyList()),
                        followerCount = followersResult.getOrDefault(0).toLong(),
                        followingCount = followingResult.getOrDefault(0).toLong(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "Error loading profile data", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
