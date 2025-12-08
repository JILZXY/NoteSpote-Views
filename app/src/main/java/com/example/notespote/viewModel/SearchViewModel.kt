package com.example.notespote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notespote.domain.model.FiltroApuntes
import com.example.notespote.domain.usecases.auth.GetCurrentUserUseCase
import com.example.notespote.domain.usecases.folders.SearchCarpetasUseCase
import com.example.notespote.domain.usecases.notes.SearchApuntesUseCase
import com.example.notespote.viewModel.states.SearchFilter
import com.example.notespote.viewModel.states.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val searchApuntesUseCase: SearchApuntesUseCase,
    private val searchCarpetasUseCase: SearchCarpetasUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadCurrentUser()
        setupSearchDebounce()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().firstOrNull()?.onSuccess { usuario ->
                currentUserId = usuario?.id
                if (currentUserId != null) {
                    performSearch()
                }
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _uiState
                .debounce(300)
                .distinctUntilChanged { old, new ->
                    old.searchQuery == new.searchQuery && old.selectedFilters == new.selectedFilters
                }
                .collect {
                    performSearch()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleFilter(filter: SearchFilter) {
        val currentFilters = _uiState.value.selectedFilters.toMutableSet()
        if (currentFilters.contains(filter)) {
            currentFilters.remove(filter)
        } else {
            currentFilters.add(filter)
        }
        _uiState.value = _uiState.value.copy(selectedFilters = currentFilters)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(selectedFilters = emptySet())
    }

    private fun performSearch() {
        val userId = currentUserId ?: return
        val query = _uiState.value.searchQuery
        val filters = _uiState.value.selectedFilters

        if (query.isBlank() && filters.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                carpetas = emptyList(),
                apuntes = emptyList(),
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val shouldSearchCarpetas = filters.isEmpty() || filters.contains(SearchFilter.CARPETAS)
                val shouldSearchApuntes = filters.isEmpty() || filters.contains(SearchFilter.APUNTES)

                if (shouldSearchCarpetas) {
                    searchCarpetasUseCase(userId, query).collect { result ->
                        result.onSuccess { carpetas ->
                            _uiState.value = _uiState.value.copy(
                                carpetas = carpetas,
                                isLoading = false
                            )
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                error = error.message,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(carpetas = emptyList())
                }

                if (shouldSearchApuntes) {
                    val filtroApuntes = FiltroApuntes(
                        query = query.takeIf { it.isNotBlank() },
                        soloConImagenes = filters.contains(SearchFilter.SOLO_IMAGENES),
                        soloConPostits = filters.contains(SearchFilter.SOLO_POSTITS)
                    )

                    searchApuntesUseCase(filtroApuntes).collect { result ->
                        result.onSuccess { apuntes ->
                            _uiState.value = _uiState.value.copy(
                                apuntes = apuntes,
                                isLoading = false
                            )
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                error = error.message,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(apuntes = emptyList())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        loadCurrentUser()
    }
}
