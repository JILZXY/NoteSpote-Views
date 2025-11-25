package com.example.notespote.presentation.views

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.MajorelleBlue
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.UrbanistFamily
import com.example.notespote.presentation.theme.YellowOrange
import kotlin.math.roundToInt


data class OnboardingPage(
    val title: String,
    val description: String
)

@Composable
fun PreloadView(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Crea y Gestiona Tus Notas Fácilmente",
            description = "Es un hecho establecido que un lector se distraerá con el contenido legible de una página al observar su diseño. El objetivo de usar"
        ),
        OnboardingPage(
            title = "Organiza Tus Notas Eficientemente",
            description = "Mantén todas tus notas organizadas por categorías y encuéntralas al instante cuando las necesites"
        ),
        OnboardingPage(
            title = "Comparte Conocimiento Con Otros",
            description = "Conéctate con otros estudiantes y comparte tus notas para ayudarse mutuamente a tener éxito"
        )
    )

    var currentPage by remember { mutableStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_offset_y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_notespot_black),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .padding(bottom = 100.dp)
                .offset { IntOffset(0, offsetY.roundToInt()) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = RichBlack,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pages[currentPage].title,
                        fontFamily = UrbanistFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pages[currentPage].description,
                        fontFamily = UrbanistFamily,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pages.forEachIndexed { index, _ ->
                            val width by animateDpAsState(
                                targetValue = if (index == currentPage) 32.dp else 8.dp,
                                label = "indicator_width"
                            )
                            Box(
                                modifier = Modifier
                                    .size(width, 8.dp)
                                    .background(
                                        color = when (index) {
                                            0 -> YellowOrange
                                            1 -> Celeste
                                            2 -> MajorelleBlue
                                            else -> Color.Gray
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (currentPage < pages.size - 1) {
                                currentPage++
                            } else {
                                onNavigateToLogin()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MajorelleBlue
                        )
                    ) {
                        Text(
                            text = if (currentPage < pages.size - 1) "Siguiente" else "Comencemos",
                            fontFamily = UrbanistFamily,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes una cuenta? ",
                            fontFamily = UrbanistFamily,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Regístrate",
                            fontFamily = UrbanistFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}
