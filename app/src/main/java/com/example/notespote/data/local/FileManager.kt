package com.example.notespote.data.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val filesDir = context.filesDir

    suspend fun saveFile(
        apunteId: String,
        fileName: String,
        inputStream: InputStream
    ): String = withContext(Dispatchers.IO) {
        val apunteDir = File(filesDir, "apuntes/$apunteId")
        if (!apunteDir.exists()) {
            apunteDir.mkdirs()
        }

        val file = File(apunteDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        file.absolutePath
    }

    suspend fun saveFileFromUri(
        apunteId: String,
        fileName: String,
        uri: Uri
    ): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            saveFile(apunteId, fileName, inputStream)
        } ?: throw IllegalStateException("No se pudo abrir el archivo")
    }

    suspend fun getFile(path: String): File? = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.exists()) file else null
    }

    suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        File(path).delete()
    }

    suspend fun deleteApunteFiles(apunteId: String): Boolean = withContext(Dispatchers.IO) {
        val apunteDir = File(filesDir, "apuntes/$apunteId")
        apunteDir.deleteRecursively()
    }

    suspend fun getFileUri(path: String): Uri? = withContext(Dispatchers.IO) {
        val file = getFile(path) ?: return@withContext null
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    suspend fun getFileSize(path: String): Int = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.exists()) {
            (file.length() / 1024).toInt()
        } else {
            0
        }
    }

    fun getFileSizeFromUri(uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                (inputStream.available() / 1024)
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun getFileExtension(uri: Uri): String {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            when (mimeType) {
                "image/jpeg", "image/jpg" -> "jpg"
                "image/png" -> "png"
                "image/gif" -> "gif"
                "application/pdf" -> "pdf"
                "application/msword" -> "doc"
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
                "text/plain" -> "txt"
                else -> {
                    // Intentar obtener del nombre del archivo
                    val fileName = getFileName(uri)
                    fileName.substringAfterLast('.', "")
                }
            }
        } ?: ""
    }

    fun getFileName(uri: Uri): String {
        var result = "archivo_${System.currentTimeMillis()}"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            result = cursor.getString(nameIndex)
        }
        return result
    }

    fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "application/octet-stream"
    }
}
