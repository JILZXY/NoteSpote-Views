package com.example.notespote.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.domain.model.Note
import com.example.notespote.presentation.components.buttons.FloatingActionButtons
import com.example.notespote.presentation.components.cards.WelcomeCard
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily

@Composable
fun HomeView(
    userName: String = "Naimur",
    onCreateFolderClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAddNoteClick: () -> Unit
) {
    var showNoteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Celeste),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.first().toString(),
                            fontFamily = UrbanistFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = RichBlack
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            text = "Hola, $userName",
                            fontFamily = UrbanistFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 27.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Vamos a explorar tus apuntes",
                            fontFamily = SyneMonoFamily,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            WelcomeCard(
                onAddNoteClick = { showNoteDialog = true },
                onCreateFolderClick = onCreateFolderClick
            )

            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "Escritorio",
                fontFamily = UrbanistFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Tu escritorio está vacío. Agrega una nota o una carpeta para iniciar.",
                    fontFamily = UrbanistFamily,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        FloatingActionButtons(
            onAddNoteClick = { showNoteDialog = true },
            onCreateFolderClick = onCreateFolderClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 100.dp)
        )

        if (showNoteDialog) {
            NewNoteView(
                onDismiss = { showNoteDialog = false },
                onCreateNote = { note ->
                    // Here you would typically handle the created note, e.g., pass to a ViewModel
                    showNoteDialog = false
                }
            )
        }
    }
}
