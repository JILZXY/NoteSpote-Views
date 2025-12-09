package com.example.notespote.presentation.views

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.PanTool
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.presentation.theme.OutfitFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import kotlin.math.roundToInt

// ==================== DATA CLASSES ====================

data class DrawingStroke(
    val points: List<Offset>,
    val color: Color = Color.Black,
    val strokeWidth: Float = 5f
)

data class ManipulableObject(
    val id: String,
    var offsetX: Float,
    var offsetY: Float,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    val type: ObjectType,
    var text: String = ""
)

enum class ObjectType {
    POST_IT_YELLOW,
    POST_IT_PINK,
    IMAGE
}

enum class ToolMode {
    HAND,
    PENCIL,
    MOVE_OBJECTS
}

// ==================== COMPONENTE PRINCIPAL ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContentView(
    apunteDetallado: ApunteDetallado,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (String, String, List<String>) -> Unit,
    onAddTag: (String) -> Unit,
    onAddText: () -> Unit,
    onUploadFile: () -> Unit,
    onAddImage: () -> Unit,
    onDrawClick: () -> Unit,
    onOpenFile: ((String) -> Unit)? = null
) {
    Log.d("NoteContentView", "Rendering with Infinite Canvas")

    // ==================== ESTADOS ====================
    var currentTool by remember { mutableStateOf(ToolMode.HAND) }
    var isDarkMode by remember { mutableStateOf(true) }

    // Contenido de texto
    var freeTextContent by remember(apunteDetallado.apunte.id) {
        mutableStateOf(apunteDetallado.apunte.contenido ?: "")
    }

    // Buscar PDF
    val pdfPath = remember(apunteDetallado.archivos) {
        apunteDetallado.archivos
            .firstOrNull { it.extension.equals("pdf", ignoreCase = true) }
            ?.rutaLocal
    }

    val isPdfMode = pdfPath != null

    // Estados para dibujo
    val drawingStrokes = remember { mutableStateListOf<DrawingStroke>() }
    var currentPath by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }

    // Objetos manipulables
    val manipulableObjects = remember { mutableStateListOf<ManipulableObject>() }

    val backgroundColor = if (isDarkMode) Color(0xFF0A0A0A) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val scrollState = rememberScrollState()

    // Guardar automáticamente con debounce
    LaunchedEffect(freeTextContent) {
        delay(1000) // Espera 1 segundo después de que el usuario deja de escribir
        if (!isPdfMode && freeTextContent != apunteDetallado.apunte.contenido) {
            Log.d("NoteContentView", "Auto-saving content...")
            onSaveClick(apunteDetallado.apunte.titulo, freeTextContent, emptyList())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // ==================== CAPA 1: FONDO (Z-Index: 0) ====================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            if (isPdfMode) {
                PdfBackgroundLayer(
                    pdfPath = pdfPath,
                    currentTool = currentTool,
                    scrollState = scrollState
                )
            } else {
                TextBackgroundLayer(
                    textContent = freeTextContent,
                    onTextChange = { freeTextContent = it },
                    currentTool = currentTool,
                    scrollState = scrollState,
                    isDarkMode = isDarkMode
                )
            }
        }

        // ==================== CAPA 2: DIBUJO (Z-Index: 1) ====================
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .pointerInput(currentTool) {
                    if (currentTool == ToolMode.PENCIL) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath = mutableListOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPath.add(change.position)
                            },
                            onDragEnd = {
                                if (currentPath.isNotEmpty()) {
                                    drawingStrokes.add(
                                        DrawingStroke(points = currentPath.toList())
                                    )
                                    currentPath = mutableListOf()
                                }
                            }
                        )
                    }
                }
        ) {
            drawingStrokes.forEach { stroke ->
                if (stroke.points.size > 1) {
                    val path = Path()
                    path.moveTo(stroke.points[0].x, stroke.points[0].y)
                    for (i in 1 until stroke.points.size) {
                        path.lineTo(stroke.points[i].x, stroke.points[i].y)
                    }
                    drawPath(path, stroke.color, style = Stroke(width = stroke.strokeWidth))
                }
            }

            if (currentPath.size > 1) {
                val path = Path()
                path.moveTo(currentPath[0].x, currentPath[0].y)
                for (i in 1 until currentPath.size) {
                    path.lineTo(currentPath[i].x, currentPath[i].y)
                }
                drawPath(path, Color.Black, style = Stroke(width = 5f))
            }
        }

        // ==================== CAPA 3: OBJETOS (Z-Index: 2) ====================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            for (obj in manipulableObjects) {
                key(obj.id) {
                    ManipulableObjectItem(
                        obj = obj,
                        currentTool = currentTool,
                        onTextChange = { newText ->
                            obj.text = newText
                        }
                    )
                }
            }
        }

        // ==================== TOP APP BAR (Z-Index: 3) ====================
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(3f),
            title = {
                Text(
                    text = if (isPdfMode) "Modo PDF" else "Modo Texto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = OutfitFamily
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textColor
                    )
                }
            },
            actions = {
                IconButton(onClick = onUploadFile) {
                    Icon(
                        imageVector = Icons.Filled.PictureAsPdf,
                        contentDescription = "Cargar PDF",
                        tint = Color(0xFFE91E63)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = textColor
            )
        )

        // ==================== TOOLBAR INFERIOR (Z-Index: 4) ====================
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(4f),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mano
                IconButton(
                    onClick = { currentTool = ToolMode.HAND },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (currentTool == ToolMode.HAND) Color(0xFFE3F2FD) else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PanTool,
                        contentDescription = "Mano",
                        tint = if (currentTool == ToolMode.HAND) Color(0xFF1976D2) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Lápiz
                IconButton(
                    onClick = { currentTool = ToolMode.PENCIL },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (currentTool == ToolMode.PENCIL) Color(0xFFFFF3E0) else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Draw,
                        contentDescription = "Lápiz",
                        tint = if (currentTool == ToolMode.PENCIL) Color(0xFFFF6F00) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Mover
                IconButton(
                    onClick = { currentTool = ToolMode.MOVE_OBJECTS },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (currentTool == ToolMode.MOVE_OBJECTS) Color(0xFFF3E5F5) else Color.Transparent,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = "Mover",
                        tint = if (currentTool == ToolMode.MOVE_OBJECTS) Color(0xFF7B1FA2) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Imagen
                SmallFloatingActionButton(
                    onClick = {
                        if (currentTool == ToolMode.MOVE_OBJECTS) {
                            manipulableObjects.add(
                                ManipulableObject(
                                    id = UUID.randomUUID().toString(),
                                    offsetX = 100f,
                                    offsetY = 300f,
                                    type = ObjectType.IMAGE
                                )
                            )
                        } else {
                            onAddImage()
                        }
                    },
                    containerColor = Color(0xFF90CAF9),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Imagen",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Post-it
                SmallFloatingActionButton(
                    onClick = {
                        manipulableObjects.add(
                            ManipulableObject(
                                id = UUID.randomUUID().toString(),
                                offsetX = 200f,
                                offsetY = 400f,
                                type = ObjectType.POST_IT_YELLOW
                            )
                        )
                    },
                    containerColor = Color(0xFFFFEB3B),
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Filled.StickyNote2,
                        contentDescription = "Post-it",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==================== CAPA 1: MODO TEXTO ====================

@Composable
fun TextBackgroundLayer(
    textContent: String,
    onTextChange: (String) -> Unit,
    currentTool: ToolMode,
    scrollState: androidx.compose.foundation.ScrollState,
    isDarkMode: Boolean
) {
    val modifier = if (currentTool == ToolMode.HAND) {
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    } else {
        Modifier.fillMaxSize()
    }

    Box(modifier = modifier.padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(80.dp))

            BasicTextField(
                value = textContent,
                onValueChange = { if (currentTool == ToolMode.HAND) onTextChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                enabled = currentTool == ToolMode.HAND,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = OutfitFamily,
                    color = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black,
                    lineHeight = 24.sp
                ),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (textContent.isEmpty()) {
                            Text(
                                "Comienza a escribir aquí...",
                                fontSize = 16.sp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                fontFamily = OutfitFamily
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ==================== CAPA 1: MODO PDF ====================

@Composable
fun PdfBackgroundLayer(
    pdfPath: String?,
    currentTool: ToolMode,
    scrollState: androidx.compose.foundation.ScrollState
) {
    val scope = rememberCoroutineScope()
    var pdfBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pdfPath) {
        if (pdfPath != null) {
            scope.launch {
                isLoading = true
                error = null
                withContext(Dispatchers.IO) {
                    try {
                        val file = File(pdfPath)
                        if (!file.exists()) {
                            error = "PDF no encontrado"
                            isLoading = false
                            return@withContext
                        }

                        val parcelFileDescriptor = ParcelFileDescriptor.open(
                            file,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                        val bitmaps = mutableListOf<Bitmap>()

                        for (i in 0 until pdfRenderer.pageCount) {
                            val page = pdfRenderer.openPage(i)
                            val bitmap = Bitmap.createBitmap(
                                page.width * 2,
                                page.height * 2,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmaps.add(bitmap)
                            page.close()
                        }

                        pdfBitmaps = bitmaps
                        pdfRenderer.close()
                        parcelFileDescriptor.close()
                        isLoading = false
                    } catch (e: Exception) {
                        Log.e("PdfBackgroundLayer", "Error: ${e.message}", e)
                        error = "Error: ${e.message}"
                        isLoading = false
                    }
                }
            }
        }
    }

    val modifier = if (currentTool == ToolMode.HAND) {
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    } else {
        Modifier.fillMaxSize()
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(200.dp))
                    CircularProgressIndicator(color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando PDF...", color = Color.Gray, fontFamily = OutfitFamily)
                }
                error != null -> {
                    Spacer(modifier = Modifier.height(100.dp))
                    Text(error!!, color = Color.Red, fontFamily = OutfitFamily)
                }
                pdfBitmaps.isNotEmpty() -> {
                    pdfBitmaps.forEachIndexed { index, bitmap ->
                        Spacer(modifier = Modifier.height(if (index == 0) 0.dp else 24.dp))

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Página ${index + 1}",
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

// ==================== CAPA 3: OBJETOS ====================

@Composable
fun ManipulableObjectItem(
    obj: ManipulableObject,
    currentTool: ToolMode,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(obj.offsetX.roundToInt(), obj.offsetY.roundToInt()) }
            .scale(obj.scale)
            .rotate(obj.rotation)
            .pointerInput(currentTool, obj.id) {
                if (currentTool == ToolMode.MOVE_OBJECTS) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        obj.offsetX += pan.x
                        obj.offsetY += pan.y
                        obj.scale *= zoom
                        obj.rotation += rotation
                    }
                }
            }
    ) {
        when (obj.type) {
            ObjectType.POST_IT_YELLOW -> {
                PostItView(
                    text = obj.text,
                    onTextChange = onTextChange,
                    backgroundColor = Color(0xFFFFEB3B),
                    borderColor = Color(0xFFFDD835),
                    enabled = currentTool == ToolMode.HAND
                )
            }
            ObjectType.POST_IT_PINK -> {
                PostItView(
                    text = obj.text,
                    onTextChange = onTextChange,
                    backgroundColor = Color(0xFFFF80AB),
                    borderColor = Color(0xFFF50057),
                    enabled = currentTool == ToolMode.HAND
                )
            }
            ObjectType.IMAGE -> {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color(0xFF90CAF9), RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFF42A5F5), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Imagen",
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PostItView(
    text: String,
    onTextChange: (String) -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { if (enabled) onTextChange(it) },
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = OutfitFamily
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (text.isEmpty()) {
                        Text("Nota...", fontSize = 12.sp, color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )
    }
}
