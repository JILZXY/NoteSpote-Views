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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notespote.domain.model.Note
import com.example.notespote.presentation.components.dialogs.CancelConfirmationDialog
import com.example.notespote.presentation.components.dialogs.SuccessConfirmationDialog
import com.example.notespote.presentation.theme.OutfitFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNoteView(
    onDismiss: () -> Unit,
    onCreateNote: (Note) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Otros") }
    var customSubject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showCustomSubject by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf<String?>(null) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdNoteData by remember { mutableStateOf<Note?>(null) }

    val subjects = listOf("Matemáticas", "Química", "Biología", "Otros")
    val hasContent = title.isNotBlank() || description.isNotBlank() || (showCustomSubject && customSubject.isNotBlank())

    if (showCancelDialog) {
        CancelConfirmationDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = onDismiss
        )
    }

    if (showSuccessDialog && createdNoteData != null) {
        SuccessConfirmationDialog(
            noteData = createdNoteData!!,
            onDismiss = {
                showSuccessDialog = false
                onDismiss()
            }
        )
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
                        text = "Nuevo apunte",
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
                        placeholder = { Text("Ej. Apunte Cálculo", fontFamily = OutfitFamily, fontSize = 14.sp, color = Color.Gray) },
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
                        text = "Materia",
                        style = TextStyle(fontSize = 18.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        TextField(
                            value = if (showCustomSubject) customSubject else subject,
                            onValueChange = { customSubject = it },
                            readOnly = !showCustomSubject,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .height(48.dp)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                            textStyle = TextStyle(fontSize = 14.sp, fontFamily = OutfitFamily, color = Color.Black),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                disabledTextColor = Color.Black // Show text color when readOnly
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color.White)
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            subjects.forEachIndexed { index, subjectOption ->
                                DropdownMenuItem(
                                    text = { Text(subjectOption, fontFamily = OutfitFamily, color = Color.Black) },
                                    onClick = {
                                        if (subjectOption == "Otros") {
                                            showCustomSubject = true
                                            subject = ""
                                        } else {
                                            subject = subjectOption
                                            showCustomSubject = false
                                        }
                                        isDropdownExpanded = false
                                    }
                                )
                                if (index < subjects.lastIndex) {
                                    Divider(color = Color.LightGray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apunte de texto", fontSize = 12.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Outlined.Upload, contentDescription = null, Modifier.height(16.dp))
                                Text("Subir archivo", fontSize = 12.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descripción",
                        style = TextStyle(fontSize = 18.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, color = Color.Black),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Escribe la descripción de tu apunte aquí...", fontSize = 14.sp, color = Color.Gray, fontFamily = OutfitFamily) },
                        modifier = Modifier.fillMaxWidth().height(100.dp).border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = OutfitFamily, color = Color.Black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Hacer público", style = TextStyle(fontSize = 16.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, color = Color.Black))
                            Text("Los apuntes públicos serán visibles para toda la comunidad", style = TextStyle(fontSize = 11.sp, fontFamily = OutfitFamily, color = Color.Gray, lineHeight = 14.sp))
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
                                    val finalSubject = if (showCustomSubject) customSubject else subject
                                    val newNote = Note(title, finalSubject, description, isPublic)
                                    onCreateNote(newNote)
                                    createdNoteData = newNote
                                    showSuccessDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90EE90), contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Crear apunte", fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { if (hasContent) showCancelDialog = true else onDismiss() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5800), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Cancelar", fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
