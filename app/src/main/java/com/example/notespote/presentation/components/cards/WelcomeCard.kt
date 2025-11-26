package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily

@Composable
fun WelcomeCard(
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(164.dp)
            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            .background(Color(0x1CFFFFFF), RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.libromascota),
            contentDescription = null,
            modifier = Modifier.size(110.dp).align(Alignment.CenterEnd),
            alpha = 0.9f
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFFFB347))) {
                            append("B")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFFF9E9E))) {
                            append("i")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFFFF599))) {
                            append("e")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF91F48F))) {
                            append("n")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF9EFFFF))) {
                            append("v")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFFD99FF))) {
                            append("e")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFB69CFF))) {
                            append("n")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF624AF2))) {
                            append("i")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFFCDDEC))) {
                            append("d")
                        }
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append("o")
                        }
                        append(" a NoteSpot")
                    },
                    fontFamily = UrbanistFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Crea tu primer apunte o carpeta para iniciar la magia.",
                    fontFamily = SyneMonoFamily,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 14.sp,
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .width(110.dp)
                        .height(33.dp)
                        .background(
                            color = Color(0xFFFD99FF),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onAddNoteClick() }
                        .padding(start = 4.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        alignment = Alignment.TopStart,
                        modifier = Modifier
                            .padding(1.dp)
                            .size(22.dp)
                    )
                    Text(
                        text = "Añadir Nota",
                        fontFamily = UrbanistFamily,
                        fontSize = 12.sp,
                        color = RichBlack,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .width(100.dp)
                        .height(33.dp)
                        .background(
                            color = Color(0xFFFFF599),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onCreateFolderClick() }
                        .padding(start = 4.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier
                            .padding(1.dp)
                            .size(30.dp)
                    )
                    Text(
                        text = "Crear carpeta",
                        fontFamily = UrbanistFamily,
                        fontSize = 12.sp,
                        color = RichBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
