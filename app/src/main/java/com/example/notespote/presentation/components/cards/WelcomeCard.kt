package com.example.notespot.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily
import com.example.notespote.presentation.theme.VioletWeb
import com.example.notespote.presentation.theme.YellowOrange

@Composable
fun WelcomeCard(
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, Celeste, RoundedCornerShape(16.dp))
            .background(RichBlack, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = YellowOrange)) {
                            append("Bien")
                        }
                        withStyle(style = SpanStyle(color = VioletWeb)) {
                            append("venido")
                        }
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append(" a NoteSpot")
                        }
                    },
                    fontFamily = UrbanistFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crea tu primer apunte o carpeta para iniciar la magia.",
                    fontFamily = SyneMonoFamily,
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddNoteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VioletWeb
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Añadir Nota",
                            fontFamily = UrbanistFamily,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onCreateFolderClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowOrange
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = RichBlack
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Crear carpeta",
                            fontFamily = UrbanistFamily,
                            fontSize = 12.sp,
                            color = RichBlack
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.mascot_notespot),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}