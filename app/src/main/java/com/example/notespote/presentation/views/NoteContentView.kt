package com.example.notespote.presentation.views

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
import org.json.JSONArray
import org.json.JSONObject
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
    val offsetX: Float,
    val offsetY: Float,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val type: ObjectType,
    val text: String = "",
    val color: Long = 0xFFFFF59D,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val fontSize: Float = 14f
)

enum class ObjectType {
    POST_IT,
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
    Log.d("NoteContentView", "Rendering Final Corrected Version")

    // ==================== ESTADOS DE UI ====================
    var currentTool by remember { mutableStateOf(ToolMode.HAND) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var isDarkMode by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Estados de formato
    var currentFontSize by remember { mutableStateOf(16f) }
    var isBoldActive by remember { mutableStateOf(false) }
    var isItalicActive by remember { mutableStateOf(false) }

    val colorPalette = listOf(Color.Black, Color.Red, Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFF9C27B0), Color.White)

    // ==================== ESTADOS DE DATOS ====================

    val initialData = remember(apunteDetallado.apunte.id) {
        val rawContent = apunteDetallado.apunte.contenido ?: ""
        parseNoteContent(rawContent)
    }

    var textFieldValue by remember { mutableStateOf(initialData.first) }
    val drawingStrokes = remember { mutableStateListOf<DrawingStroke>().apply { addAll(initialData.second) } }
    val manipulableObjects = remember { mutableStateListOf<ManipulableObject>().apply { addAll(initialData.third) } }

    var currentPath by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }

    // Buscar PDF
    val pdfPath = remember(apunteDetallado.archivos) {
        apunteDetallado.archivos.firstOrNull { it.extension.equals("pdf", ignoreCase = true) }?.rutaLocal
    }
    val isPdfMode = pdfPath != null

    val backgroundColor = if (isDarkMode) Color(0xFF0A0A0A) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black

    // ==================== LÓGICA DE FORMATO ====================

    fun applyStyleToSelection(bold: Boolean? = null, italic: Boolean? = null, size: Float? = null) {
        val selection = textFieldValue.selection
        if (selection.collapsed) return

        val builder = AnnotatedString.Builder(textFieldValue.annotatedString)
        builder.addStyle(
            SpanStyle(
                fontWeight = if (bold == true) FontWeight.Bold else if (bold == false) FontWeight.Normal else null,
                fontStyle = if (italic == true) FontStyle.Italic else if (italic == false) FontStyle.Normal else null,
                fontSize = size?.sp ?: currentFontSize.sp

            ),
            selection.start,
            selection.end
        )
        textFieldValue = textFieldValue.copy(annotatedString = builder.toAnnotatedString())
    }

    // ==================== AUTO-GUARDADO ====================
    LaunchedEffect(textFieldValue, drawingStrokes.size, manipulableObjects.toList()) {
        delay(1000)
        val contentString = serializeNoteContent(textFieldValue, drawingStrokes, manipulableObjects)
        if (contentString != apunteDetallado.apunte.contenido) {
            onSaveClick(apunteDetallado.apunte.titulo, contentString, emptyList())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {
        // ==================== ÁREA DE CONTENIDO ====================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState, enabled = currentTool == ToolMode.HAND)
        ) {
            // --- CAPA 1: FONDO (Texto/PDF) ---
            // Z-Index 0 para que siempre esté atrás y no tape a los objetos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, bottom = 160.dp)
                    .zIndex(0f)
            ) {
                if (isPdfMode) {
                    PdfBackgroundLayer(pdfPath = pdfPath)
                } else {
                    TextBackgroundLayer(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        isDarkMode = isDarkMode,
                        enabled = currentTool == ToolMode.HAND
                    )
                }
            }

            // --- CAPA 2: DIBUJO ---
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(1f)
                    .then(if (currentTool == ToolMode.PENCIL) Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { currentPath = mutableListOf(it) },
                            onDrag = { change, _ -> change.consume(); currentPath.add(change.position) },
                            onDragEnd = {
                                if (currentPath.isNotEmpty()) {
                                    drawingStrokes.add(DrawingStroke(currentPath.toList(), selectedColor))
                                    currentPath = mutableListOf()
                                }
                            }
                        )
                    } else Modifier)
            ) {
                drawingStrokes.forEach { s ->
                    if (s.points.size > 1) {
                        val path = Path().apply {
                            moveTo(s.points[0].x, s.points[0].y)
                            (1 until s.points.size).forEach { i -> lineTo(s.points[i].x, s.points[i].y) }
                        }
                        drawPath(path, s.color, style = Stroke(s.strokeWidth))
                    }
                }
                if (currentPath.size > 1) {
                    val path = Path().apply {
                        moveTo(currentPath[0].x, currentPath[0].y)
                        (1 until currentPath.size).forEach { i -> lineTo(currentPath[i].x, currentPath[i].y) }
                    }
                    drawPath(path, selectedColor, style = Stroke(5f))
                }
            }

            // --- CAPA 3: OBJETOS ---
            Box(modifier = Modifier.matchParentSize().zIndex(2f)) {
                manipulableObjects.forEachIndexed { index, obj ->
                    key(obj.id) {
                        ManipulableObjectItem(
                            obj = obj,
                            currentTool = currentTool,
                            onUpdate = { manipulableObjects[index] = it },
                            onDelete = { manipulableObjects.removeAt(index) },
                            // Si estamos editando el post-it, aplicamos el estilo seleccionado
                            onApplyStyle = {
                                manipulableObjects[index] = obj.copy(
                                    isBold = isBoldActive,
                                    isItalic = isItalicActive,
                                    fontSize = currentFontSize
                                )
                            }
                        )
                    }
                }
            }
        }

        // ==================== UI FIJA ====================
        TopAppBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = { Text(if (isPdfMode) "Modo PDF" else "Modo Texto", fontSize = 18.sp, fontFamily = OutfitFamily) },
            navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = textColor) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor.copy(0.95f), titleContentColor = textColor)
        )

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                // BARRA DE COLORES (Lápiz o Mover/Crear)
                if (currentTool == ToolMode.PENCIL || currentTool == ToolMode.MOVE_OBJECTS || currentTool == ToolMode.HAND) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorPalette.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp).clip(CircleShape).background(color)
                                    .border(if (selectedColor == color) 3.dp else 1.dp, if (selectedColor == color) Color.Black.copy(0.5f) else Color.Gray, CircleShape)
                                    .clickable { selectedColor = color }
                            )
                        }
                    }
                    HorizontalDivider()
                }

                // HERRAMIENTAS
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentTool = ToolMode.HAND }, modifier = Modifier.background(if (currentTool == ToolMode.HAND) Color(0xFFE3F2FD) else Color.Transparent, CircleShape)) {
                        Icon(Icons.Outlined.PanTool, null, tint = if (currentTool == ToolMode.HAND) Color(0xFF1976D2) else Color.Gray)
                    }
                    IconButton(onClick = { currentTool = ToolMode.PENCIL }, modifier = Modifier.background(if (currentTool == ToolMode.PENCIL) Color(0xFFFFF3E0) else Color.Transparent, CircleShape)) {
                        Icon(Icons.Outlined.Draw, null, tint = if (currentTool == ToolMode.PENCIL) Color(0xFFFF6F00) else Color.Gray)
                    }
                    IconButton(onClick = { currentTool = ToolMode.MOVE_OBJECTS }, modifier = Modifier.background(if (currentTool == ToolMode.MOVE_OBJECTS) Color(0xFFF3E5F5) else Color.Transparent, CircleShape)) {
                        Icon(Icons.Outlined.TouchApp, null, tint = if (currentTool == ToolMode.MOVE_OBJECTS) Color(0xFF7B1FA2) else Color.Gray)
                    }

                    SmallFloatingActionButton(onClick = {
                        val color = if (selectedColor == Color.Black || selectedColor == Color.White) Color(0xFFFFEB3B) else selectedColor
                        manipulableObjects.add(ManipulableObject(
                            id = UUID.randomUUID().toString(),
                            offsetX = 200f, offsetY = scrollState.value + 400f,
                            type = ObjectType.POST_IT, color = color.toArgb().toLong(),
                            isBold = isBoldActive, isItalic = isItalicActive, fontSize = currentFontSize
                        ))
                        currentTool = ToolMode.MOVE_OBJECTS
                    }, containerColor = selectedColor) { Icon(Icons.Filled.StickyNote2, null, tint = if (selectedColor.luminance() > 0.5f) Color.Black else Color.White) }
                }
            }
        }
    }
}

// ==================== LAYERS ====================

@Composable
fun TextBackgroundLayer(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isDarkMode: Boolean,
    enabled: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 2000.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            enabled = enabled,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = OutfitFamily, color = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black, lineHeight = 24.sp),
            decorationBox = { if (value.text.isEmpty()) Text("Escribe aquí...", color = Color.Gray); it() }
        )
    }
}

@Composable
fun PdfBackgroundLayer(pdfPath: String?) {
    val scope = rememberCoroutineScope()
    var pdfBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    LaunchedEffect(pdfPath) {
        if (pdfPath != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val file = File(pdfPath)
                    if (file.exists()) {
                        val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                        val renderer = PdfRenderer(pfd)
                        val bitmaps = mutableListOf<Bitmap>()
                        for (i in 0 until renderer.pageCount) {
                            val page = renderer.openPage(i)
                            val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                            bmp.eraseColor(android.graphics.Color.WHITE) // Fondo blanco obligado
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmaps.add(bmp)
                            page.close()
                        }
                        pdfBitmaps = bitmaps
                        renderer.close(); pfd.close()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(min = 2000.dp)) {
        if (pdfBitmaps.isEmpty()) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(50.dp))
        else pdfBitmaps.forEach { bmp ->
            Image(bitmap = bmp.asImageBitmap(), null, modifier = Modifier.fillMaxWidth().aspectRatio(bmp.width.toFloat() / bmp.height), contentScale = ContentScale.FillWidth)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(800.dp))
    }
}

@Composable
fun ManipulableObjectItem(
    obj: ManipulableObject,
    currentTool: ToolMode,
    onUpdate: (ManipulableObject) -> Unit,
    onDelete: () -> Unit,
    onApplyStyle: () -> Unit
) {
    val currentObj by rememberUpdatedState(obj)

    // Detectamos si el post-it está siendo editado
    if (currentTool == ToolMode.HAND && obj.type == ObjectType.POST_IT) {
        // Podríamos aplicar estilo aquí si tiene foco, pero usamos el botón global
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(currentObj.offsetX.roundToInt(), currentObj.offsetY.roundToInt()) }
            .scale(currentObj.scale)
            .rotate(currentObj.rotation)
            .then(if (currentTool == ToolMode.MOVE_OBJECTS) Modifier.pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    onUpdate(currentObj.copy(offsetX = currentObj.offsetX + pan.x, offsetY = currentObj.offsetY + pan.y, scale = currentObj.scale * zoom, rotation = currentObj.rotation + rotation))
                }
            } else Modifier)
            // Si estamos en modo Mano y tocamos el objeto, aplicamos el estilo global actual
            .clickable(enabled = currentTool == ToolMode.HAND) { onApplyStyle() }
    ) {
        // Botón Eliminar
        if (currentTool == ToolMode.MOVE_OBJECTS) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = (-12).dp).size(24.dp).zIndex(3f).background(Color.Red, CircleShape)
            ) { Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
        }

        when (obj.type) {
            ObjectType.POST_IT -> PostItView(
                text = obj.text,
                style = TextStyle(
                    fontWeight = if (obj.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (obj.isItalic) FontStyle.Italic else FontStyle.Normal,
                    fontSize = obj.fontSize.sp
                ),
                color = Color(obj.color),
                enabled = currentTool == ToolMode.HAND,
                onTextChange = { onUpdate(currentObj.copy(text = it)) }
            )
            ObjectType.IMAGE -> Box(
                modifier = Modifier.size(200.dp).background(Color(0xFF90CAF9), RoundedCornerShape(12.dp)).border(2.dp, Color(0xFF42A5F5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Image, null, modifier = Modifier.size(80.dp), tint = Color.White) }
        }
    }
}

@Composable
fun PostItView(text: String, onTextChange: (String) -> Unit, style: TextStyle, color: Color, enabled: Boolean) {
    val isDark = color.luminance() < 0.5f
    val textColor = if (isDark) Color.White else Color.Black
    Box(modifier = Modifier.size(150.dp).background(color, RoundedCornerShape(8.dp)).padding(12.dp)) {
        BasicTextField(
            value = text, onValueChange = onTextChange, modifier = Modifier.fillMaxSize(), enabled = enabled,
            textStyle = style.copy(color = textColor, fontFamily = OutfitFamily),
            decorationBox = { if (text.isEmpty()) Text("Nota...", style = style.copy(color = textColor.copy(0.7f))); it() }
        )
    }
}

// ==================== SERIALIZACIÓN ====================

fun serializeNoteContent(
    textValue: TextFieldValue,
    strokes: List<DrawingStroke>,
    objects: List<ManipulableObject>
): String {
    val json = JSONObject()
    json.put("text", textValue.text)

    val spanArray = JSONArray()
    textValue.annotatedString.spanStyles.forEach { span ->
        spanArray.put(JSONObject().apply {
            put("start", span.start); put("end", span.end)
            put("bold", span.item.fontWeight == FontWeight.Bold)
            put("italic", span.item.fontStyle == FontStyle.Italic)
            put("size", span.item.fontSize.value)
        })
    }
    json.put("spans", spanArray)

    val drawingsArray = JSONArray()
    strokes.forEach { s ->
        val pArray = JSONArray()
        s.points.forEach { p -> pArray.put(JSONObject().put("x", p.x).put("y", p.y)) }
        drawingsArray.put(JSONObject().put("color", s.color.toArgb()).put("points", pArray))
    }
    json.put("drawings", drawingsArray)

    val objectsArray = JSONArray()
    objects.forEach { o ->
        objectsArray.put(JSONObject().apply {
            put("id", o.id); put("x", o.offsetX); put("y", o.offsetY)
            put("scale", o.scale); put("rot", o.rotation); put("type", o.type.name)
            put("text", o.text); put("color", o.color)
            put("bold", o.isBold); put("italic", o.isItalic); put("fSize", o.fontSize)
        })
    }
    json.put("objects", objectsArray)

    return json.toString()
}

fun parseNoteContent(jsonStr: String): Triple<TextFieldValue, List<DrawingStroke>, List<ManipulableObject>> {
    return try {
        if (!jsonStr.trim().startsWith("{")) return Triple(TextFieldValue(jsonStr), emptyList(), emptyList())
        val json = JSONObject(jsonStr)

        val rawText = json.optString("text", "")
        val spansArray = json.optJSONArray("spans")
        val builder = AnnotatedString.Builder(rawText)
        if (spansArray != null) {
            for (i in 0 until spansArray.length()) {
                val s = spansArray.getJSONObject(i)
                builder.addStyle(
                    SpanStyle(
                        fontWeight = if(s.optBoolean("bold")) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if(s.optBoolean("italic")) FontStyle.Italic else FontStyle.Normal,
                        fontSize = s.optDouble("size", 16.0).sp
                    ), s.getInt("start"), s.getInt("end")
                )
            }
        }

        val strokes = mutableListOf<DrawingStroke>()
        val dArray = json.optJSONArray("drawings")
        if (dArray != null) {
            for (i in 0 until dArray.length()) {
                val d = dArray.getJSONObject(i)
                val points = mutableListOf<Offset>()
                val pArray = d.getJSONArray("points")
                for (j in 0 until pArray.length()) {
                    val p = pArray.getJSONObject(j)
                    points.add(Offset(p.getDouble("x").toFloat(), p.getDouble("y").toFloat()))
                }
                strokes.add(DrawingStroke(points, Color(d.optInt("color", Color.Black.toArgb()))))
            }
        }

        val objects = mutableListOf<ManipulableObject>()
        val oArray = json.optJSONArray("objects")
        if (oArray != null) {
            for (i in 0 until oArray.length()) {
                val o = oArray.getJSONObject(i)
                val typeStr = o.optString("type", "POST_IT")
                val type = if(typeStr.contains("POST_IT")) ObjectType.POST_IT else ObjectType.IMAGE
                objects.add(ManipulableObject(
                    id = o.optString("id", UUID.randomUUID().toString()),
                    offsetX = o.optDouble("x", 0.0).toFloat(),
                    offsetY = o.optDouble("y", 0.0).toFloat(),
                    scale = o.optDouble("scale", 1.0).toFloat(),
                    rotation = o.optDouble("rot", 0.0).toFloat(),
                    type = type,
                    text = o.optString("text", ""),
                    color = o.optLong("color", 0xFFFFF59D),
                    isBold = o.optBoolean("bold", false),
                    isItalic = o.optBoolean("italic", false),
                    fontSize = o.optDouble("fSize", 14.0).toFloat()
                ))
            }
        }

        Triple(TextFieldValue(builder.toAnnotatedString()), strokes, objects)
    } catch (e: Exception) {
        Triple(TextFieldValue(jsonStr), emptyList(), emptyList())
    }
}

fun Color.luminance(): Float = (0.2126f * red + 0.7152f * green + 0.0722f * blue)