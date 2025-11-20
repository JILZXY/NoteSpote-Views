package com.example.notespote.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.notespote.R



val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val UrbanistFont = GoogleFont("Urbanist")
val SyneMonoFont = GoogleFont("Syne Mono")

val UrbanistFamily = FontFamily(
    Font(googleFont = UrbanistFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = UrbanistFont, fontProvider = provider, weight = FontWeight.Bold)
)

val SyneMonoFamily = FontFamily(
    Font(googleFont = SyneMonoFont, fontProvider = provider, weight = FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = UrbanistFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SyneMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
