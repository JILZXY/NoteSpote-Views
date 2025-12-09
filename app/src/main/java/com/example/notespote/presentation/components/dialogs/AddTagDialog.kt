package com.example.notespote.presentation.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var tagError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
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
                    text = "Agregar etiqueta",
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
                        .clickable { onDismiss() },
                    tint = Color(0xFFFF5800)
                )
            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Nombre de etiqueta",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = tagName,
                    onValueChange = { tagName = it; tagError = null },
                    placeholder = {
                        Text(
                            "Ej. Ejercicios, Teoría, Importante",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = OutfitFamily
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            1.dp,
                            if (tagError != null) Color.Red else Color.Black,
                            RoundedCornerShape(8.dp)
                        ),
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
                    isError = tagError != null
                )

                if (tagError != null) {
                    Text(
                        text = tagError!!,
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp, fontFamily = OutfitFamily),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (tagName.isBlank()) {
                                tagError = "El nombre no puede estar vacío"
                            } else {
                                onConfirm(tagName.trim())
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF90EE90),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("Agregar", fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5800),
                            contentColor = Color.White
                        ),
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
