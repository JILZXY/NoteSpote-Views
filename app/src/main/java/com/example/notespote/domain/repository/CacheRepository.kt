package com.example.notespote.domain.repository

interface CacheRepository {
    suspend fun <T> get(key: String): T?
    suspend fun <T> put(key: String, value: T, expirationMillis: Long? = null)
    suspend fun remove(key: String)
    suspend fun clear()
    suspend fun clearExpired()
}