package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Embedded
import androidx.room.Relation
import com.example.notespote.data.local.entities.ApunteEntity
import com.example.notespote.data.local.entities.PostitEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.EtiquetaApunteEntity
import com.example.notespote.data.local.entities.EtiquetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApunteDao {
    @Query("SELECT * FROM apunte WHERE is_deleted = 0 AND id_usuario = :userId ORDER BY fecha_creacion DESC")
    fun getApuntesByUser(userId: String): Flow<List<ApunteEntity>>

    @Query("SELECT * FROM apunte WHERE is_deleted = 0 AND id_carpeta = :folderId ORDER BY fecha_creacion DESC")
    fun getApuntesByFolder(folderId: String): Flow<List<ApunteEntity>>

    @Query("SELECT * FROM apunte WHERE id_apunte = :id AND is_deleted = 0")
    suspend fun getApunteById(id: String): ApunteEntity?

    @Query("SELECT * FROM apunte WHERE sync_status != 'SYNCED' AND is_deleted = 0")
    suspend fun getPendingSyncApuntes(): List<ApunteEntity>

    @Query("SELECT * FROM apunte WHERE tipo_visibilidad = 'PUBLICO' AND is_deleted = 0 ORDER BY total_likes DESC LIMIT :limit")
    fun getPublicApuntes(limit: Int = 50): Flow<List<ApunteEntity>>
    
    @Query("SELECT * FROM apunte WHERE is_deleted = 0 AND id_usuario = :userId ORDER BY fecha_actualizacion DESC LIMIT :limit")
    fun getRecentApuntes(userId: String, limit: Int): Flow<List<ApunteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(apunte: ApunteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apuntes: List<ApunteEntity>)

    @Update
    suspend fun update(apunte: ApunteEntity)

    @Query("UPDATE apunte SET is_deleted = 1, sync_status = 'PENDING_DELETE' WHERE id_apunte = :id")
    suspend fun markAsDeleted(id: String)

    @Query("UPDATE apunte SET sync_status = :status WHERE id_apunte = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Transaction
    @Query("SELECT * FROM apunte WHERE id_apunte = :id")
    suspend fun getApunteWithDetails(id: String): ApunteWithDetails?

    @Query("DELETE FROM apunte WHERE id_apunte = :id")
    suspend fun delete(id: String)
}

data class ApunteWithDetails(
    @Embedded val apunte: ApunteEntity,
    @Relation(
        parentColumn = "id_apunte",
        entityColumn = "id_apunte"
    )
    val postits: List<PostitEntity>,
    @Relation(
        parentColumn = "id_apunte",
        entityColumn = "id_apunte"
    )
    val archivos: List<ArchivoAdjuntoEntity>,
    @Relation(
        parentColumn = "id_apunte",
        entityColumn = "id_apunte",
        entity = EtiquetaApunteEntity::class
    )
    val etiquetas: List<EtiquetaWithName>
)

data class EtiquetaWithName(
    @Embedded val etiquetaApunte: EtiquetaApunteEntity,
    @Relation(
        parentColumn = "id_etiqueta",
        entityColumn = "id_etiqueta"
    )
    val etiqueta: EtiquetaEntity
)