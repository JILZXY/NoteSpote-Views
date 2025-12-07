package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.OutfitFamily

data class FolderCardData(
    val title: String,
    val color: Color,
    val overlayIcon: ImageVector? = null // Optional overlay icon
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderCard(
    folder: FolderCardData,
    onClick: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 130.dp, height = 100.dp)
                .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000), clip = false)
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = {
                        if (onRename != null || onDelete != null) {
                            showMenu = true
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Base folder shape from drawable
            Icon(
                painter = painterResource(id = R.drawable.ic_folder),
                contentDescription = folder.title,
                tint = folder.color,
                modifier = Modifier.matchParentSize()
            )
            // Overlay icon if it exists
            if (folder.overlayIcon != null) {
                Icon(
                    imageVector = folder.overlayIcon,
                    contentDescription = null, // Decorative
                    tint = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                )
            }

            // Context menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (onRename != null) {
                    DropdownMenuItem(
                        text = { Text("Actualizar") },
                        onClick = {
                            showMenu = false
                            onRename()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DriveFileRenameOutline,
                                contentDescription = "Actualizar"
                            )
                        }
                    )
                }
                if (onDelete != null) {
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(folder.title, color = Color.White, fontSize = 12.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold)
    }
}
