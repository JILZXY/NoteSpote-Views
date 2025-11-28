package com.example.notespote.domain.repository

import com.example.notespote.domain.model.ConfiguracionApp
import kotlinx.coroutines.flow.Flow

interface ConfiguracionRepository {
    val configuracion: Flow<ConfiguracionApp>

    suspend fun getConfiguracion(): Result<ConfiguracionApp>
    suspend fun updateConfiguracion(configuracion: ConfiguracionApp): Result<Unit>
    suspend fun updateModoOscuro(modoOscuro: Boolean): Result<Unit>
    suspend fun updateSincronizacionAutomatica(activada: Boolean): Result<Unit>
    suspend fun updateNotificaciones(activadas: Boolean): Result<Unit>
    suspend fun updateDescargarSoloWifi(soloWifi: Boolean): Result<Unit>
    suspend fun updateIdioma(idioma: String): Result<Unit>
    suspend fun syncConfiguracion(): Result<Unit>
}