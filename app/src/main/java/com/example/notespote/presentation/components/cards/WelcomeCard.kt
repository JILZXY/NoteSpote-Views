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
import androidx.compose.ui.layout.ContentScale
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
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
            verticalArrangement = Arrangement.Center
        ) {
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Usa los botones flotantes para crear tu primer apunte o carpeta.",
                fontFamily = SyneMonoFamily,
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth(0.65f)
            )
        }
    }
}