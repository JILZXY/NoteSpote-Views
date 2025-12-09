package com.example.notespote.data.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isConnected: Flow<Boolean>
    suspend fun isCurrentlyConnected(): Boolean
}