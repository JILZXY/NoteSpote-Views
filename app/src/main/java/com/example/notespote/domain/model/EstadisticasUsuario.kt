package com.example.notespote.data.model

data class EstadisticasUsuario(
    val totalApuntes: Int = 0,
    val totalApuntesPublicos: Int = 0,
    val totalApuntesPrivados: Int = 0,
    val totalLikesRecibidos: Int = 0,
    val totalSeguidores: Int = 0,
    val totalSeguidos: Int = 0,
    val totalCarpetas: Int = 0,
    val materiasMasUsadas: List<Materia> = emptyList(),
    val etiquetasMasUsadas: List<Etiqueta> = emptyList()
)