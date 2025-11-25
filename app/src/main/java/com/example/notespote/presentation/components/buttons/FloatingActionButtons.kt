package com.example.notespot.presentation.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.KhakiLight
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.VioletWeb

@Composable
fun FloatingActionButtons(
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = spring()) + expandVertically(animationSpec = spring()),
            exit = fadeOut(animationSpec = spring()) + shrinkVertically(animationSpec = spring())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    containerColor = VioletWeb
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                FloatingActionButton(
                    onClick = {
                        onCreateFolderClick()
                        isExpanded = false
                    },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    containerColor = KhakiLight
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = RichBlack
                    )
                }

                FloatingActionButton(
                    onClick = {
                        onAddNoteClick()
                        isExpanded = false
                    },
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    containerColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = RichBlack
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            containerColor = Celeste
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null,
                tint = RichBlack,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}