package com.example.notespote.domain.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadImage(uri: Uri, path: String): Result<String>
    suspend fun uploadFile(uri: Uri, path: String): Result<String>
    suspend fun downloadFile(url: String, localPath: String): Result<String>
    suspend fun deleteFile(path: String): Result<Unit>
    suspend fun getDownloadUrl(path: String): Result<String>
}