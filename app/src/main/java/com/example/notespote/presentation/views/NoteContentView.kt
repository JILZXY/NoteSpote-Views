package com.example.notespote.presentation.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.presentation.components.dialogs.AddTagDialog
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import kotlin.math.absoluteValue
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteContentView(
    apunteDetallado: ApunteDetallado,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (String, String, List<String>) -> Unit,
    onAddTag: (String) -> Unit,
    onAddText: () -> Unit,
    onUploadFile: () -> Unit,
    onAddImage: () -> Unit,
    onDrawClick: () -> Unit,
    onOpenFile: ((String) -> Unit)? = null
) {
    Log.d("NoteContentView", "Recomposing with tags: ${apunteDetallado.etiquetas.map { it.nombreEtiqueta }}")

    var isDarkMode by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }

    var editableTitle by remember { mutableStateOf(apunteDetallado.apunte.titulo) }
    var editableContent by remember { mutableStateOf(apunteDetallado.apunte.contenido ?: "") }

    val backgroundColor = Color(0xFF0A0A0A)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón atrás
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textColor
                    )
                }

                // Botones de la derecha
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Switch modo oscuro
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it },
                            modifier = Modifier.size(32.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4A4A4A),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF2A2A2A)
                            )
                        )
                    }

                    // Botón editar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), CircleShape)
                            .clickable {
                                isEditing = !isEditing
                                if (!isEditing) onEditClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Botón guardar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), CircleShape)
                            .clickable {
                                onSaveClick(editableTitle, editableContent, apunteDetallado.etiquetas.map { it.nombreEtiqueta })
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Título
                if (isEditing) {
                    TextField(
                        value = editableTitle,
                        onValueChange = { editableTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OutfitFamily,
                            color = textColor
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        placeholder = {
                            Text(
                                "Título del apunte",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    )
                } else {
                    Text(
                        text = editableTitle,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OutfitFamily,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Tags
                FlowRow(
                    modifier = Modifier.padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Add Tag
                    Button(
                        onClick = { showAddTagDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = textColor
                        ),
                        border = BorderStroke(1.dp, Color(0xFF3A3A3A)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = textColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Add Tag",
                            fontSize = 13.sp,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Tags existentes
                    apunteDetallado.etiquetas.forEach { etiqueta ->
                        val tagColor = when (etiqueta.nombreEtiqueta.lowercase()) {
                            "exercises" -> Color(0xFFB794F6)
                            "calcular" -> Color(0xFF81E6A1)
                            else -> Color(0xFF64B5F6)
                        }

                        Row(
                            modifier = Modifier
                                .height(36.dp)
                                .background(tagColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "# ${etiqueta.nombreEtiqueta}",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontFamily = OutfitFamily,
                                fontWeight = FontWeight.Medium
                            )
                            if (isEditing) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar etiqueta",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onAddTag("REMOVE:${etiqueta.id}") },
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Imágenes y archivos adjuntos
                if (apunteDetallado.archivos.isNotEmpty()) {
                    val imagenes = apunteDetallado.archivos.filter { it.esImagen }
                    val otrosArchivos = apunteDetallado.archivos.filter { !it.esImagen }

                    // Mostrar imágenes
                    if (imagenes.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            imagenes.forEach { imagen ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1A1A1A))
                                        .clickable {
                                            imagen.rutaLocal?.let { onOpenFile?.invoke(it) }
                                        }
                                ) {
                                    // Mostrar imagen real usando Coil
                                    if (imagen.rutaLocal != null) {
                                        val imageFile = File(imagen.rutaLocal)
                                        android.util.Log.d("NoteContentView", "Loading image: ${imageFile.absolutePath}, exists: ${imageFile.exists()}")

                                        AsyncImage(
                                            model = imageFile,
                                            contentDescription = imagen.nombreArchivo,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(280.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = ContentScale.Crop,
                                            placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF2A2A2A)),
                                            error = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF3A3A3A))
                                        )
                                    } else {
                                        // Placeholder si no hay ruta local
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(280.dp)
                                                .background(Color(0xFF2A2A2A)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(48.dp),
                                                    tint = Color.Gray
                                                )
                                                Text(
                                                    text = "Cargando imagen...",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = OutfitFamily
                                                )
                                            }
                                        }
                                    }

                                    // Nombre del archivo en la parte inferior
                                    if (isEditing) {
                                        Text(
                                            text = imagen.nombreArchivo,
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .background(Color.Black.copy(alpha = 0.7f))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontFamily = OutfitFamily
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Mostrar otros archivos (PDF, DOCX, etc.)
                    if (otrosArchivos.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            otrosArchivos.forEach { archivo ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E1E1E))
                                        .clickable {
                                            archivo.rutaLocal?.let { onOpenFile?.invoke(it) }
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CloudUpload,
                                            contentDescription = null,
                                            tint = Color(0xFF81E6A1),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Column {
                                            Text(
                                                text = archivo.nombreArchivo,
                                                fontSize = 14.sp,
                                                color = textColor,
                                                fontFamily = OutfitFamily,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${archivo.extension.uppercase()} • ${String.format("%.2f", archivo.tamanoMB)} MB",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                fontFamily = OutfitFamily
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    // Placeholder cuando no hay archivos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(80.dp),
                            tint = Color(0xFFE0E0E0)
                        )
                        Text(
                            text = "Sin archivos adjuntos",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 100.dp),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = OutfitFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Contenido de texto
                if (isEditing) {
                    TextField(
                        value = editableContent,
                        onValueChange = { editableContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = OutfitFamily,
                            color = Color(0xFFB0B0B0),
                            lineHeight = 22.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color(0xFFB0B0B0),
                            unfocusedTextColor = Color(0xFFB0B0B0)
                        ),
                        placeholder = {
                            Text(
                                "Escribe el contenido de tu apunte aquí...",
                                fontSize = 14.sp,
                                color = Color(0xFF606060)
                            )
                        }
                    )
                } else {
                    Text(
                        text = editableContent,
                        fontSize = 14.sp,
                        fontFamily = OutfitFamily,
                        color = Color(0xFFB0B0B0),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Diálogo de agregar tag
        if (showAddTagDialog) {
            AddTagDialog(
                onDismiss = { showAddTagDialog = false },
                onConfirm = { tagName ->
                    Log.d("NoteContentView", "onConfirm: tagName='${tagName}', existing=${apunteDetallado.etiquetas.map { it.nombreEtiqueta }}")
                    if (!apunteDetallado.etiquetas.any { it.nombreEtiqueta.equals(tagName, ignoreCase = true) }) {
                        Log.d("NoteContentView", "Tag does not exist. Adding.")
                        onAddTag(tagName)
                    } else {
                        Log.d("NoteContentView", "Tag already exists. Not adding.")
                    }
                    showAddTagDialog = false
                }
            )
        }

        // Barra inferior con iconos
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onAddText,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TextFields,
                        contentDescription = "Agregar texto",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onUploadFile,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = "Subir archivo",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onAddImage,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Agregar imagen",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onDrawClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Draw,
                        contentDescription = "Dibujar",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
