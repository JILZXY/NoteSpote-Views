package com.example.notespote.data.repository

import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.ArchivoAdjuntoDao
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.repository.ApunteRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ApunteRepositoryImpl @Inject constructor(
    private val apunteDao: ApunteDao,
    private val archivoDao: ArchivoAdjuntoDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val fileManager: FileManager,
    private val networkMonitor: NetworkMonitor,
    private val apunteMapper: ApunteMapper
) : ApunteRepository {

    override fun getApuntesByUser(userId: String): Flow<Result<List<Apunte>>> {
        return apunteDao.getApuntesByUser(userId)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun createApunte(
        apunte: Apunte,
        archivos: List<Uri>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apunteId = UUID.randomUUID().toString()
            val entity = apunteMapper.toEntity(apunte).copy(
                idApunte = apunteId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            // Guardar apunte localmente
            apunteDao.insert(entity)

            // Guardar archivos localmente
            archivos.forEachIndexed { index, uri ->
                val fileName = "archivo_$index.${getFileExtension(uri)}"
                val rutaLocal = fileManager.saveFile(apunteId, fileName,
                    context.contentResolver.openInputStream(uri)!!)

                val archivoEntity = ArchivoAdjuntoEntity(
                    idArchivo = UUID.randomUUID().toString(),
                    idApunte = apunteId,
                    nombreArchivo = fileName,
                    tipoArchivo = getMimeType(uri),
                    rutaLocal = rutaLocal,
                    urlFirebase = null,
                    tamanoKb = getFileSize(uri),
                    fechaSubida = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_UPLOAD
                )
                archivoDao.insert(archivoEntity)
            }

            // Intentar sincronizar si hay conexión
            if (networkMonitor.isConnected()) {
                syncApunte(apunteId)
            }

            Result.success(apunteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun syncApunte(apunteId: String) {
        // Implementar lógica de sincronización individual
    }