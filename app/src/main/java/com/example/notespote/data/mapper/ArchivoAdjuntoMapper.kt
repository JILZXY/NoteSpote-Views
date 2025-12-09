package com.example.notespote.data.mapper

import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.domain.model.ArchivoAdjunto
import com.example.notespote.data.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchivoAdjuntoMapper @Inject constructor() {

    fun toDomain(entity: ArchivoAdjuntoEntity): ArchivoAdjunto {
        return ArchivoAdjunto(
            id = entity.idArchivo,
            idApunte = entity.idApunte,
            nombreArchivo = entity.nombreArchivo,
            tipoArchivo = entity.tipoArchivo,
            rutaLocal = entity.rutaLocal,
            urlFirebase = entity.urlFirebase,
            tamanoKb = entity.tamanoKb,
            fechaSubida = entity.fechaSubida,
            estadoDescarga = entity.estadoDescarga
        )
    }

    fun toEntity(domain: ArchivoAdjunto): ArchivoAdjuntoEntity {
        return ArchivoAdjuntoEntity(
            idArchivo = domain.id,
            idApunte = domain.idApunte,
            nombreArchivo = domain.nombreArchivo,
            tipoArchivo = domain.tipoArchivo,
            rutaLocal = domain.rutaLocal,
            urlFirebase = domain.urlFirebase,
            tamanoKb = domain.tamanoKb,
            fechaSubida = domain.fechaSubida,
            estadoDescarga = domain.estadoDescarga,
            syncStatus = SyncStatus.PENDING_UPLOAD,
            isDeleted = false
        )
    }
}