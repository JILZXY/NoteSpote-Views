package com.example.notespote.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notespote.data.local.entities.PostitEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PostitDao {
    @Query("SELECT * FROM postit WHERE id_apunte = :apunteId AND is_deleted = 0")
    fun getPostitsByApunte(apunteId: String): Flow<List<PostitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postit: PostitEntity)

    @Update
    suspend fun update(postit: PostitEntity)

    @Query("UPDATE postit SET is_deleted = 1 WHERE id_postit = :id")
    suspend fun markAsDeleted(id: String)
}