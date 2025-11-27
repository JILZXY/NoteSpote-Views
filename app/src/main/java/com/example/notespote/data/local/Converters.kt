package com.example.notespote.data.local

import androidx.room.TypeConverter
import com.example.notespote.data.local.entities.EstadoDescarga
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.TipoVisibilidad

// data/local/Converters.kt
class Converters {
    @TypeConverter
    fun fromTipoVisibilidad(value: TipoVisibilidad): String = value.name

    @TypeConverter
    fun toTipoVisibilidad(value: String): TipoVisibilidad =
        TipoVisibilidad.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus =
        SyncStatus.valueOf(value)

    @TypeConverter
    fun fromEstadoDescarga(value: EstadoDescarga): String = value.name

    @TypeConverter
    fun toEstadoDescarga(value: String): EstadoDescarga =
        EstadoDescarga.valueOf(value)
}