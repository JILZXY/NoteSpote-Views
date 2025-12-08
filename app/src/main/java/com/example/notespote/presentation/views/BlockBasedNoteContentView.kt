package com.example.notespote.presentation.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NoteAdd
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.model.NoteBlock
import com.example.notespote.presentation.components.blocks.*
import com.example.notespote.presentation.components.dialogs.AddTagDialog
import com.example.notespote.presentation.theme.OutfitFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlockBasedNoteContentView(
    apunteDetallado: ApunteDetallado,
    noteBlocks: List<NoteBlock>,
    onBackClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onAddTextBlock: () -> Unit,
    onAddPostipBlock: () -> Unit,
    onUploadFile: () -> Unit,
    onAddImage: () -> Unit,
    onDrawClick: () -> Unit,
    onOpenFile: ((String) -> Unit)? = null,
    onBlockContentChange: (String, String) -> Unit,
    onBlockDelete: (String) -> Unit
) {
    Log.d("BlockBasedNoteContentView", "Recomposing with ${noteBlocks.size} blocks")

    var isEditing by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var showBlockMenu by remember { mutableStateOf(false) }

    var editableTitle by remember(apunteDetallado.apunte.titulo) {
        mutableStateOf(apunteDetallado.apunte.titulo)
    }

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
            // Header con botones circulares
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón atrás
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1E1E1E), CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Botón editar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E1E1E), CircleShape)
                            .clickable {
                                isEditing = !isEditing
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
                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF1E1E1E), CircleShape)
                                .clickable {
                                    onSaveClick(editableTitle)
                                    isEditing = false
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
            }

            // LazyColumn con bloques
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Título
                item {
                    if (isEditing) {
                        TextField(
                            value = editableTitle,
                            onValueChange = { editableTitle = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
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
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                // Etiquetas (Tags)
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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

                        // Botón agregar tag
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF2A2A2A))
                                    .clickable { showAddTagDialog = true }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar tag",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Gray
                                    )
                                    Text(
                                        "Tag",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontFamily = OutfitFamily
                                    )
                                }
                            }
                        }
                    }
                }

                // Renderizar bloques
                items(noteBlocks, key = { it.id }) { block ->
                    when (block) {
                        is NoteBlock.TextBlock -> {
                            TextBlockItem(
                                block = block,
                                isEditing = isEditing,
                                onContentChange = { newContent ->
                                    onBlockContentChange(block.id, newContent)
                                },
                                onDelete = { onBlockDelete(block.id) }
                            )
                        }

                        is NoteBlock.ImageBlock -> {
                            ImageBlockItem(
                                block = block,
                                isEditing = isEditing,
                                onDelete = { onBlockDelete(block.id) },
                                onOpenFile = { onOpenFile?.invoke(block.rutaLocal) }
                            )
                        }

                        is NoteBlock.PdfBlock -> {
                            PdfBlockItem(
                                block = block,
                                isEditing = isEditing,
                                onDelete = { onBlockDelete(block.id) },
                                onOpenFile = { onOpenFile?.invoke(block.rutaLocal) }
                            )
                        }

                        is NoteBlock.PostipBlock -> {
                            PostipBlockItem(
                                block = block,
                                isEditing = isEditing,
                                onContentChange = { newContent ->
                                    onBlockContentChange(block.id, newContent)
                                },
                                onDelete = { onBlockDelete(block.id) }
                            )
                        }
                    }
                }

                // Botón para agregar bloques (en modo edición)
                if (isEditing) {
                    item {
                        OutlinedButton(
                            onClick = { showBlockMenu = !showBlockMenu },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF3A3A3A))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar bloque",
                                tint = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Agregar bloque",
                                color = textColor,
                                fontFamily = OutfitFamily
                            )
                        }

                        // Menú de bloques
                        if (showBlockMenu) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                BlockMenuItem(
                                    icon = Icons.Outlined.TextFields,
                                    text = "Texto",
                                    onClick = {
                                        onAddTextBlock()
                                        showBlockMenu = false
                                    }
                                )
                                BlockMenuItem(
                                    icon = Icons.Outlined.StickyNote2,
                                    text = "Post-it",
                                    onClick = {
                                        onAddPostipBlock()
                                        showBlockMenu = false
                                    }
                                )
                                BlockMenuItem(
                                    icon = Icons.Outlined.Image,
                                    text = "Imagen",
                                    onClick = {
                                        onAddImage()
                                        showBlockMenu = false
                                    }
                                )
                                BlockMenuItem(
                                    icon = Icons.Outlined.CloudUpload,
                                    text = "Archivo",
                                    onClick = {
                                        onUploadFile()
                                        showBlockMenu = false
                                    }
                                )
                                BlockMenuItem(
                                    icon = Icons.Outlined.Draw,
                                    text = "Dibujo",
                                    onClick = {
                                        onDrawClick()
                                        showBlockMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Diálogo agregar tag
        if (showAddTagDialog) {
            AddTagDialog(
                onDismiss = { showAddTagDialog = false },
                onConfirm = { tagName ->
                    onAddTag(tagName)
                    showAddTagDialog = false
                }
            )
        }
    }
}

@Composable
private fun BlockMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.White,
            fontFamily = OutfitFamily
        )
    }
}
