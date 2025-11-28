package com.example.notespote.domain.model

import com.example.notespote.data.local.entities.TipoVisibilidad

data class FiltroApuntes(
    val query: String? = null,
    val idMateria: String? = null,
    val idCarpeta: String? = null,
    val etiquetas: List<String> = emptyList(),
    val tipoVisibilidad: TipoVisibilidad? = null,
    val ordenamiento: com.example.notespote.domain.model.OrdenApuntes = _root_ide_package_.com.example.notespote.domain.model.OrdenApuntes.FECHA_DESC,
    val soloConImagenes: Boolean = false,
    val soloConPostits: Boolean = false
)

enum class OrdenApuntes {
    FECHA_DESC,
    FECHA_ASC,
    TITULO_ASC,
    TITULO_DESC,
    MAS_LIKES,
    MAS_GUARDADOS
}