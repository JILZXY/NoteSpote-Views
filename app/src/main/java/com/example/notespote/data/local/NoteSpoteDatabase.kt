package com.example.notespote.data.local

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.ArchivoAdjuntoDao
import com.example.notespote.data.local.dao.CarpetaDao
import com.example.notespote.data.local.dao.EtiquetaApunteDao
import com.example.notespote.data.local.dao.EtiquetaDao
import com.example.notespote.data.local.dao.LikeDao
import com.example.notespote.data.local.dao.MateriaDao
import com.example.notespote.data.local.dao.PostitDao
import com.example.notespote.data.local.dao.SeguimientoDao
import com.example.notespote.data.local.dao.UsuarioDao
import com.example.notespote.data.local.entities.UsuarioEntity
import com.example.notespote.data.local.entities.ApunteEntity
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.SeguimientoEntity
import com.example.notespote.data.local.entities.CarpetaEntity
import com.example.notespote.data.local.entities.MateriaEntity
import com.example.notespote.data.local.entities.PostitEntity
import com.example.notespote.data.local.entities.EtiquetaApunteEntity
import com.example.notespote.data.local.entities.EtiquetaEntity
import com.example.notespote.data.local.entities.LikeEntity

@Database(
    entities = [
        UsuarioEntity::class,
        MateriaEntity::class,
        CarpetaEntity::class,
        ApunteEntity::class,
        PostitEntity::class,
        ArchivoAdjuntoEntity::class,
        LikeEntity::class,
        EtiquetaEntity::class,
        EtiquetaApunteEntity::class,
        SeguimientoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NoteSpotDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun materiaDao(): MateriaDao
    abstract fun carpetaDao(): CarpetaDao
    abstract fun apunteDao(): ApunteDao
    abstract fun postitDao(): PostitDao
    abstract fun archivoAdjuntoDao(): ArchivoAdjuntoDao
    abstract fun likeDao(): LikeDao
    abstract fun etiquetaDao(): EtiquetaDao
    abstract fun etiquetaApunteDao(): EtiquetaApunteDao
    abstract fun seguimientoDao(): SeguimientoDao
}