package com.example.notespote.presentation.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.notespote.domain.model.Folder
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun SuccessFolderDialog(folderData: Folder, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Éxito",
                tint = Color(0xFF8BC34A), // Green color
                modifier = Modifier.height(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Carpeta Creada!",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Título: ${folderData.title}", fontFamily = OutfitFamily, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Color:", fontFamily = OutfitFamily, color = Color.Black)
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(folderData.color)))
                    )
                }
                Text("Público: ${if (folderData.isPublic) "Sí" else "No"}", fontFamily = OutfitFamily, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A), contentColor = Color.Black),
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Aceptar", color = Color.Black, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
            }
        }
    }
}
