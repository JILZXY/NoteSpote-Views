package com.example.notespote.domain.usecases.archivos

import android.net.Uri
import com.example.notespote.data.local.FileManager
import com.example.notespote.data.local.dao.ArchivoAdjuntoDao
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.EstadoDescarga
import com.example.notespote.data.local.entities.SyncStatus
import java.util.UUID
import javax.inject.Inject

class AddArchivosToApunteUseCase @Inject constructor(
    private val archivoDao: ArchivoAdjuntoDao,
    private val fileManager: FileManager
) {
    suspend operator fun invoke(apunteId: String, archivos: List<Uri>): Result<Unit> {
        return try {
            android.util.Log.d("AddArchivosToApunteUseCase", "Adding ${archivos.size} files to apunte: $apunteId")
            archivos.forEachIndexed { index, uri ->
                val fileName = fileManager.getFileName(uri)
                val rutaLocal = fileManager.saveFileFromUri(apunteId, fileName, uri)
                android.util.Log.d("AddArchivosToApunteUseCase", "File saved: $fileName at $rutaLocal")

                val archivoEntity = ArchivoAdjuntoEntity(
                    idArchivo = UUID.randomUUID().toString(),
                    idApunte = apunteId,
                    nombreArchivo = fileName,
                    tipoArchivo = fileManager.getMimeType(uri),
                    rutaLocal = rutaLocal,
                    urlFirebase = null,
                    tamanoKb = fileManager.getFileSizeFromUri(uri),
                    fechaSubida = System.currentTimeMillis(),
                    estadoDescarga = EstadoDescarga.DESCARGADO,
                    syncStatus = SyncStatus.PENDING_UPLOAD,
                    isDeleted = false
                )
                archivoDao.insert(archivoEntity)
                android.util.Log.d("AddArchivosToApunteUseCase", "File entity inserted: ${archivoEntity.idArchivo}")
            }
            android.util.Log.d("AddArchivosToApunteUseCase", "All files added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AddArchivosToApunteUseCase", "Error adding files", e)
            Result.failure(e)
        }
    }
}
