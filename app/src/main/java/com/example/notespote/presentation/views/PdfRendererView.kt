package com.example.notespote.presentation.views

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfRendererView(pdfPath: String) {
    val context = LocalContext.current
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pdfPath) {
        scope.launch {
            isLoading = true
            withContext(Dispatchers.IO) {
                val file = File(pdfPath)
                if (file.exists()) {
                    try {
                        val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                        val newBitmaps = mutableListOf<Bitmap>()
                        for (i in 0 until pdfRenderer.pageCount) {
                            val page = pdfRenderer.openPage(i)
                            // Set a higher quality bitmap config
                            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            newBitmaps.add(bitmap)
                            page.close()
                        }
                        bitmaps = newBitmaps
                        pdfRenderer.close()
                        parcelFileDescriptor.close()
                    } catch (e: Exception) {
                        Log.e("PdfRendererView", "Error rendering PDF", e)
                        error = "Error rendering PDF: ${e.message}"
                    }
                } else {
                    error = "File not found: $pdfPath"
                }
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = error!!, color = Color.Red)
            }
        }
        bitmaps.isNotEmpty() -> {
            Column {
                bitmaps.forEach { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null, // Decorative
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                            .padding(vertical = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}