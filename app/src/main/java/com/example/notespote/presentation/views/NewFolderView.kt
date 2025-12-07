package com.example.notespote.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notespote.domain.model.Folder
import com.example.notespote.presentation.components.dialogs.CancelConfirmationDialog
import com.example.notespote.presentation.components.dialogs.SuccessFolderDialog
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun NewFolderView(
    onDismiss: () -> Unit,
    onCreateFolder: (Folder) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFFFFB347)) } // Default Orange
    var isPublic by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdFolderData by remember { mutableStateOf<Folder?>(null) }

    val hasContent = title.isNotBlank()

    val availableColors = listOf(
        Color(0xFFF48FB1), Color(0xFFFFB347), Color(0xFFFFF599), Color(0xFF91F48F),
        Color(0xFF80DEEA), Color(0xFF9EFFFF), Color(0xFFB39DDB), Color(0xFFE1BEE7)
    )

    if (showCancelDialog) {
        CancelConfirmationDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = onDismiss
        )
    }
    if (showSuccessDialog && createdFolderData != null) {
        SuccessFolderDialog(folderData = createdFolderData!!, onDismiss = {
            showSuccessDialog = false
            onDismiss()
        })
    }

    if (!showSuccessDialog) {
        Dialog(onDismissRequest = { if (hasContent) showCancelDialog = true else onDismiss() }) {
            Column(
                modifier = Modifier
                    .width(340.dp)
                    .wrapContentHeight()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nueva carpeta",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { if (hasContent) showCancelDialog = true else onDismiss() },
                        tint = Color(0xFFFF5800)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Título",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TextField(
                        value = title,
                        onValueChange = { title = it; titleError = null },
                        placeholder = { Text("Ej. Apuntes de Cálculo", fontSize = 14.sp, color = Color.Gray, fontFamily = OutfitFamily) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(1.dp, if (titleError != null) Color.Red else Color.Black, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = OutfitFamily, color = Color.Black),
                        singleLine = true,
                        isError = titleError != null
                    )
                    if (titleError != null) {
                        Text(
                            text = titleError!!,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp, fontFamily = OutfitFamily),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Color",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = color }
                                    .border(
                                        width = if (selectedColor == color) 2.dp else 0.dp,
                                        color = if (selectedColor == color) Color.Black else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Hacer pública", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = OutfitFamily))
                            Text("Las carpetas públicas serán visibles para toda la comunidad", style = TextStyle(fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp, fontFamily = OutfitFamily))
                        }
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF8BC34A),
                                uncheckedThumbColor = Color.Black,
                                uncheckedTrackColor = Color.White,
                                uncheckedBorderColor = Color.Black
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    titleError = "El título no puede estar vacío"
                                } else {
                                    // Convertir color a formato hexadecimal correcto (ARGB)
                                    val colorHex = selectedColor.toArgb().toUInt().toString(16).padStart(8, '0')
                                    android.util.Log.d("NewFolderView", "Selected color ARGB: #$colorHex")
                                    val newFolder = Folder(title, "#$colorHex", isPublic)
                                    onCreateFolder(newFolder)
                                    createdFolderData = newFolder
                                    showSuccessDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90), contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Crear carpeta", fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
                        }
                        Button(
                            onClick = { if (hasContent) showCancelDialog = true else onDismiss() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5800), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
                        }
                    }
                }
            }
        }
    }
}
