package com.example.notespote.presentation.components.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.notespote.domain.model.NoteBlock
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.views.PdfRendererView
import java.io.File

/**
 * Bloque de texto editable
 */
@Composable
fun TextBlockItem(
    block: NoteBlock.TextBlock,
    isEditing: Boolean,
    onContentChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(block.content) { mutableStateOf(block.content) }

    Box(modifier = modifier.fillMaxWidth()) {
        if (isEditing) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    onContentChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = OutfitFamily,
                    color = Color.White
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1A1A1A),
                    unfocusedContainerColor = Color(0xFF1A1A1A),
                    focusedBorderColor = Color(0xFF3A3A3A),
                    unfocusedBorderColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                placeholder = {
                    Text(
                        "Escribe aquí...",
                        color = Color.Gray,
                        fontFamily = OutfitFamily
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = Color.Gray
                        )
                    }
                }
            )
        } else {
            Text(
                text = block.content.ifEmpty { "Texto vacío" },
                fontSize = 16.sp,
                color = if (block.content.isEmpty()) Color.Gray else Color.White,
                fontFamily = OutfitFamily,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Bloque de imagen
 */
@Composable
fun ImageBlockItem(
    block: NoteBlock.ImageBlock,
    isEditing: Boolean,
    onDelete: () -> Unit,
    onOpenFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenFile() }
    ) {
        AsyncImage(
            model = File(block.rutaLocal),
            contentDescription = block.nombreArchivo,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF2A2A2A)),
            error = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF3A3A3A))
        )

        // Botón de eliminar en modo edición
        if (isEditing) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar imagen",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Bloque de PDF
 */
@Composable
fun PdfBlockItem(
    block: NoteBlock.PdfBlock,
    isEditing: Boolean,
    onDelete: () -> Unit,
    onOpenFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(40.dp)
                )
                Column {
                    Text(
                        text = block.nombreArchivo,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "PDF • ${String.format("%.2f", block.tamanoKb / 1024f)} MB",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = OutfitFamily
                    )
                }
            }

            Row {
                TextButton(onClick = { onOpenFile() }) {
                    Text("View")
                }
                if (isEditing) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar PDF",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PdfRendererView(pdfPath = block.rutaLocal)
    }
}


/**
 * Bloque Post-it (nota adhesiva)
 */
@Composable
fun PostipBlockItem(
    block: NoteBlock.PostipBlock,
    isEditing: Boolean,
    onContentChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(block.content) { mutableStateOf(block.content) }
    val backgroundColor = Color(android.graphics.Color.parseColor(block.colorHex))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con título "Post-it" y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📌 Post-it",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontFamily = OutfitFamily
                )

                if (isEditing) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar Post-it",
                            tint = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido editable
            if (isEditing) {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onContentChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = OutfitFamily,
                        color = Color.Black.copy(alpha = 0.8f)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black.copy(alpha = 0.8f),
                        unfocusedTextColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    placeholder = {
                        Text(
                            "Escribe una nota rápida...",
                            color = Color.Black.copy(alpha = 0.4f),
                            fontFamily = OutfitFamily,
                            fontSize = 15.sp
                        )
                    },
                    minLines = 3
                )
            } else {
                Text(
                    text = block.content.ifEmpty { "Post-it vacío" },
                    fontSize = 15.sp,
                    color = if (block.content.isEmpty())
                        Color.Black.copy(alpha = 0.4f)
                    else
                        Color.Black.copy(alpha = 0.8f),
                    fontFamily = OutfitFamily,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
