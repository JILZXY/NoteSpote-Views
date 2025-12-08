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
    onDrawClick: () -> Unit
) {
    Log.d("NoteContentView", "Recomposing with tags: ${apunteDetallado.etiquetas.map { it.nombreEtiqueta }}")

    var isDarkMode by remember { mutableStateOf(true) } // Default to dark mode
    var isEditing by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }

    var editableTitle by remember { mutableStateOf(apunteDetallado.apunte.titulo) }
    var editableContent by remember { mutableStateOf(apunteDetallado.apunte.contenido ?: "") }

    val backgroundColor = if (isDarkMode) RichBlack else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val secondaryTextColor = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color.Gray

    val tagColors = remember {
        listOf(
            Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5),
            Color(0xFF2196F3), Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
            Color(0xFFFF9800), Color(0xFF795548)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textColor
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.Gray,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )

                    IconButton(onClick = {
                        isEditing = !isEditing
                        if (!isEditing) onEditClick()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            tint = textColor
                        )
                    }

                    IconButton(onClick = {
                        onSaveClick(editableTitle, editableContent, apunteDetallado.etiquetas.map { it.nombreEtiqueta })
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = textColor
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFF000000),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isEditing) {
                    TextField(
                        value = editableTitle,
                        onValueChange = { editableTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textStyle = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OutfitFamily,
                            color = textColor
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = textColor,
                            unfocusedIndicatorColor = textColor.copy(alpha = 0.5f),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        placeholder = { Text("Título del apunte", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = secondaryTextColor) }
                    )
                } else {
                    Text(
                        text = editableTitle,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OutfitFamily,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                FlowRow(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddTagDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = textColor),
                        border = BorderStroke(1.dp, secondaryTextColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Tag", fontSize = 12.sp, fontFamily = OutfitFamily)
                    }

                    apunteDetallado.etiquetas.forEach { etiqueta ->
                        val color = remember(etiqueta.nombreEtiqueta) {
                            tagColors[etiqueta.nombreEtiqueta.hashCode().absoluteValue % tagColors.size]
                        }
                        Row(
                            modifier = Modifier
                                .height(32.dp)
                                .border(width = 1.dp, color = color, shape = RoundedCornerShape(16.dp))
                                .background(
                                    color = color.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "#${etiqueta.nombreEtiqueta}",
                                fontSize = 12.sp,
                                color = color,
                                fontFamily = OutfitFamily,
                                fontWeight = FontWeight.Medium
                            )
                            if (isEditing) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar etiqueta",
                                    modifier = Modifier.size(16.dp).clickable { onAddTag("REMOVE:${etiqueta.id}") },
                                    tint = color
                                )
                            }
                        }
                    }
                }

                if (apunteDetallado.apunte.tieneImagenes || apunteDetallado.archivos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center).size(64.dp),
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isEditing) {
                    TextField(
                        value = editableContent,
                        onValueChange = { editableContent = it },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = OutfitFamily, color = textColor, lineHeight = 20.sp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = textColor,
                            unfocusedIndicatorColor = textColor.copy(alpha = 0.5f),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        placeholder = { Text("Escribe el contenido de tu apunte aquí...", fontSize = 14.sp, color = secondaryTextColor) }
                    )
                } else {
                    Text(text = editableContent, fontSize = 14.sp, fontFamily = OutfitFamily, color = secondaryTextColor, lineHeight = 20.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

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

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAddText) { Icon(imageVector = Icons.Outlined.TextFields, contentDescription = "Agregar texto", tint = Color.Black) }
                IconButton(onClick = onUploadFile) { Icon(imageVector = Icons.Outlined.CloudUpload, contentDescription = "Subir archivo", tint = Color.Black) }
                IconButton(onClick = onAddImage) { Icon(imageVector = Icons.Outlined.Image, contentDescription = "Agregar imagen", tint = Color.Black) }
                IconButton(onClick = onDrawClick) { Icon(imageVector = Icons.Outlined.Draw, contentDescription = "Dibujar", tint = Color.Black) }
            }
        }
    }
}
