package com.example.notespote.di

// --- IMPORTACIONES NECESARIAS ---
import android.content.Context
import androidx.work.WorkManager
import com.example.notespote.data.local.NoteSpotDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

// --- ANOTACIÓN PERSONALIZADA (QUALIFIER) ---
// La usamos para poder inyectar un CoroutineScope específico y evitar ambigüedades
// si en el futuro tuvieras otros Scopes.
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- PROVEEDOR DEL COROUTINESCOPE A NIVEL DE APLICACIÓN ---
    @Provides
    @Singleton
    @ApplicationScope // Usamos nuestra anotación personalizada
    fun provideApplicationScope(): CoroutineScope {
        // SupervisorJob() asegura que si una corrutina hija falla, no cancelará las demás.
        // Dispatchers.Default es ideal para tareas que consumen CPU, como el procesamiento de datos.
        // Si la mayoría de tus tareas son de red/disco, Dispatchers.IO también es una buena opción.
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    // --- PROVEEDORES PARA LAS DEPENDENCIAS DE FIREBASE Y ROOM ---
    // Hilt no sabe cómo crear estas instancias por sí mismo, así que se lo enseñamos aquí.


    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    // Nota: Los Mappers y FileManager, que tienen @Inject constructor,
    // no necesitan ser provistos aquí, ya que Hilt sabe cómo crearlos.
}
