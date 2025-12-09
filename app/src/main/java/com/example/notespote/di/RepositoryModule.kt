package com.example.notespote.di

import com.example.notespote.data.repository.ApunteRepositoryImpl
import com.example.notespote.data.repository.AuthRepositoryImpl
import com.example.notespote.data.repository.CarpetaRepositoryImpl
import com.example.notespote.data.repository.EtiquetaRepositoryImpl
import com.example.notespote.data.repository.LikeRepositoryImpl
import com.example.notespote.data.repository.MateriaRepositoryImpl
import com.example.notespote.data.repository.PostitRepositoryImpl
import com.example.notespote.data.repository.SeguimientoRepositoryImpl
import com.example.notespote.data.repository.SyncRepositoryImpl
import com.example.notespote.data.repository.UsuarioRepositoryImpl
import com.example.notespote.domain.repository.ApunteRepository
import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.CarpetaRepository
import com.example.notespote.domain.repository.EtiquetaRepository
import com.example.notespote.domain.repository.LikeRepository
import com.example.notespote.domain.repository.MateriaRepository
import com.example.notespote.domain.repository.PostitRepository
import com.example.notespote.domain.repository.SeguimientoRepository
import com.example.notespote.domain.repository.SyncRepository
import com.example.notespote.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindApunteRepository(
        apunteRepositoryImpl: ApunteRepositoryImpl
    ): ApunteRepository

    @Binds
    @Singleton
    abstract fun bindCarpetaRepository(
        carpetaRepositoryImpl: CarpetaRepositoryImpl
    ): CarpetaRepository

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        usuarioRepositoryImpl: UsuarioRepositoryImpl
    ): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindEtiquetaRepository(
        etiquetaRepositoryImpl: EtiquetaRepositoryImpl
    ): EtiquetaRepository

    @Binds
    @Singleton
    abstract fun bindLikeRepository(
        likeRepositoryImpl: LikeRepositoryImpl
    ): LikeRepository

    @Binds
    @Singleton
    abstract fun bindPostitRepository(
        postitRepositoryImpl: PostitRepositoryImpl
    ): PostitRepository

    @Binds
    @Singleton
    abstract fun bindSeguimientoRepository(
        seguimientoRepositoryImpl: SeguimientoRepositoryImpl
    ): SeguimientoRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindMateriaRepository(
        materiaRepositoryImpl: MateriaRepositoryImpl
    ): MateriaRepository
}
