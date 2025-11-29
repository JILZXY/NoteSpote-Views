package com.example.notespote.data.mapper

// data/mapper/EtiquetaMapper.kt
import com.example.notespote.data.local.entities.EtiquetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.model.Etiqueta
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EtiquetaMapper @Inject constructor() {

    fun toDomain(entity: EtiquetaEntity): Etiqueta {
        return Etiqueta(
            id = entity.idEtiqueta,
            nombreEtiqueta = entity.nombreEtiqueta,
            vecesUsada = entity.vecesUsada
        )
    }

    fun toEntity(domain: Etiqueta): EtiquetaEntity {
        return EtiquetaEntity(
            idEtiqueta = domain.id,
            nombreEtiqueta = domain.nombreEtiqueta,
            vecesUsada = domain.vecesUsada,
            syncStatus = SyncStatus.SYNCED
        )
    }
}