package com.example.notespote.data.repository

import androidx.work.WorkManager
import com.example.notespote.data.local.NoteSpotDatabase
import com.example.notespote.data.network.NetworkMonitor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.notespote.data.local.FileManager
import com.example.notespote.data.local.entities.ApunteEntity
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.remote.dto.ApunteDto
import com.example.notespote.data.mapper.toDto
import com.example.notespote.data.mapper.toEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class SyncManager @Inject constructor(
    private val database: NoteSpotDatabase,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkMonitor: NetworkMonitor,
    private val workManager: WorkManager,
    private val fileManager: FileManager,
    @ApplicationContext private val context: Context
) {

    suspend fun syncAll() {
        if (!networkMonitor.isCurrentlyConnected()) {
            Log.d("SyncManager", "No hay conexión a internet")
            return
        }

        coroutineScope {
            launch { syncUsuarios() }
            launch { syncMaterias() }
            launch { syncCarpetas() }
            launch { syncApuntes() }
            launch { syncLikes() }
            launch { syncSeguimientos() }
        }
    }

    private suspend fun syncApuntes() {
        try {
            // 1. Subir cambios locales pendientes
            val pendingApuntes = database.apunteDao().getPendingSyncApuntes()

            pendingApuntes.forEach { apunte ->
                when (apunte.syncStatus) {
                    SyncStatus.PENDING_UPLOAD -> uploadApunte(apunte)
                    SyncStatus.PENDING_UPDATE -> updateApunteFirestore(apunte)
                    SyncStatus.PENDING_DELETE -> deleteApunteFirestore(apunte)
                    else -> {}
                }
            }

            // 2. Descargar cambios del servidor
            downloadApuntesFromFirestore()
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing apuntes", e)
        }
    }

    private suspend fun uploadApunte(apunte: ApunteEntity) {
        try {
            // Subir archivos adjuntos primero
            val archivos = database.archivoAdjuntoDao()
                .getArchivosByApunte(apunte.idApunte).first()

            archivos.forEach { archivo ->
                if (archivo.rutaLocal != null && archivo.urlFirebase == null) {
                    val url = uploadFileToStorage(archivo)
                    database.archivoAdjuntoDao().update(
                        archivo.copy(
                            urlFirebase = url,
                            syncStatus = SyncStatus.SYNCED
                        )
                    )
                }
            }

            // Subir postits
            val postits = database.postitDao()
                .getPostitsByApunte(apunte.idApunte).first()

            postits.forEach { postit ->
                val postitDto = postit.toDto()
                firestore.collection("apuntes")
                    .document(apunte.idApunte)
                    .collection("postits")
                    .document(postit.idPostit)
                    .set(postitDto)
                    .await()
            }

            // Subir etiquetas
            val etiquetas = database.etiquetaApunteDao()
                .getEtiquetasByApunte(apunte.idApunte).first()

            etiquetas.forEach { etiquetaApunte ->
                val etiqueta = database.etiquetaDao()
                    .getEtiquetaById(etiquetaApunte.idEtiqueta)

                etiqueta?.let {
                    val etiquetaDto = mapOf(
                        "nombreEtiqueta" to it.nombreEtiqueta,
                        "isDeleted" to false
                    )
                    firestore.collection("apuntes")
                        .document(apunte.idApunte)
                        .collection("etiquetas")
                        .document(it.idEtiqueta)
                        .set(etiquetaDto)
                        .await()
                }
            }

            // Convertir a DTO y subir a Firestore
            val dto = apunte.toDto()
            firestore.collection("apuntes")
                .document(apunte.idApunte)
                .set(dto)
                .await()

            // Marcar como sincronizado
            database.apunteDao().updateSyncStatus(
                apunte.idApunte,
                SyncStatus.SYNCED
            )

            Log.d("SyncManager", "Apunte ${apunte.idApunte} uploaded successfully")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error uploading apunte ${apunte.idApunte}", e)
            // Marcar como conflicto si falla después de varios intentos
        }
    }

    private suspend fun updateApunteFirestore(apunte: ApunteEntity) {
        try {
            val dto = apunte.toDto()
            firestore.collection("apuntes")
                .document(apunte.idApunte)
                .set(dto, SetOptions.merge())
                .await()

            database.apunteDao().updateSyncStatus(
                apunte.idApunte,
                SyncStatus.SYNCED
            )

            Log.d("SyncManager", "Apunte ${apunte.idApunte} updated successfully")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error updating apunte ${apunte.idApunte}", e)
        }
    }

    private suspend fun deleteApunteFirestore(apunte: ApunteEntity) {
        try {
            // Soft delete en Firestore
            firestore.collection("apuntes")
                .document(apunte.idApunte)
                .update("isDeleted", true)
                .await()

            // Hard delete local
            database.apunteDao().delete(apunte.idApunte)

            Log.d("SyncManager", "Apunte ${apunte.idApunte} deleted successfully")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error deleting apunte ${apunte.idApunte}", e)
        }
    }

    private suspend fun downloadApuntesFromFirestore() {
        try {
            val userId = getCurrentUserId() ?: return

            // Obtener apuntes del usuario y públicos
            val userApuntes = firestore.collection("apuntes")
                .whereEqualTo("idUsuario", userId)
                .whereEqualTo("isDeleted", false)
                .get()
                .await()

            userApuntes.documents.forEach { doc ->
                val dto = doc.toObject(ApunteDto::class.java) ?: return@forEach
                val entity = dto.toEntity()
                database.apunteDao().insert(entity.copy(syncStatus = SyncStatus.SYNCED))
            }

            Log.d("SyncManager", "Downloaded ${userApuntes.size()} apuntes from Firestore")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error downloading apuntes", e)
        }
    }

    private suspend fun uploadFileToStorage(archivo: ArchivoAdjuntoEntity): String {
        val file = File(archivo.rutaLocal!!)
        val storageRef = storage.reference
            .child("archivos/${archivo.idApunte}/${archivo.idArchivo}_${archivo.nombreArchivo}")

        storageRef.putFile(file.toUri()).await()
        return storageRef.downloadUrl.await().toString()
    }

    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    private suspend fun syncUsuarios() {
        // Implementar según necesidad
    }

    private suspend fun syncMaterias() {
        // Implementar según necesidad
    }

    private suspend fun syncCarpetas() {
        // Implementar según necesidad
    }

    private suspend fun syncLikes() {
        // Implementar según necesidad
    }

    private suspend fun syncSeguimientos() {
        // Implementar según necesidad
    }
}