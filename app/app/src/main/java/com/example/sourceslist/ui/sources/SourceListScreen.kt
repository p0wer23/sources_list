package com.example.sourceslist.ui.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import com.example.sourceslist.ui.common.copyLink
import com.example.sourceslist.ui.common.openLink

@Composable
fun SourceListScreen(
    viewModel: SourceViewModel,
    bracket: BracketType,
    showCompleted: Boolean
) {
    val sources by if (showCompleted) {
        viewModel.completedSources(bracket).collectAsState(initial = emptyList())
    } else {
        viewModel.activeSources(bracket).collectAsState(initial = emptyList())
    }

    if (sources.isEmpty()) {
        EmptyState(bracket = bracket, showCompleted = showCompleted)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sources, key = { it.sourceId }) { source ->
                SourceItem(
                    source = source,
                    showCompleted = showCompleted,
                    onMove = viewModel::moveSource,
                    onDone = viewModel::markDone,
                    onRestore = viewModel::restore,
                    onDelete = viewModel::delete
                )
            }
        }
    }
}

@Composable
private fun EmptyState(bracket: BracketType, showCompleted: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (showCompleted) {
                "No completed ${bracket.label.lowercase()} sources"
            } else {
                "No ${bracket.label.lowercase()} sources"
            },
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SourceItem(
    source: SourceEntity,
    showCompleted: Boolean,
    onMove: (SourceEntity, BracketType) -> Unit,
    onDone: (SourceEntity) -> Unit,
    onRestore: (SourceEntity) -> Unit,
    onDelete: (SourceEntity) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val displayTitle = source.title?.takeIf { it.isNotBlank() } ?: source.url

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (source.isDone) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (source.title?.isNotBlank() == true) {
                        Text(
                            text = source.url,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    BracketType.entries
                        .filter { it != source.bracket }
                        .forEach { bracket ->
                            DropdownMenuItem(
                                text = { Text("Move to ${bracket.label}") },
                                onClick = {
                                    expanded = false
                                    onMove(source, bracket)
                                }
                            )
                        }
                    if (!showCompleted && source.bracket != BracketType.UNCLASSIFIED) {
                        DropdownMenuItem(
                            text = { Text("Mark done") },
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                            onClick = {
                                expanded = false
                                onDone(source)
                            }
                        )
                    }
                    if (showCompleted) {
                        DropdownMenuItem(
                            text = { Text("Restore") },
                            onClick = {
                                expanded = false
                                onRestore(source)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = {
                            expanded = false
                            confirmDelete = true
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(source.bracket.label) }
                )
                if (source.isDone) {
                    ElevatedAssistChip(
                        onClick = {},
                        label = { Text("Done") },
                        leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
                    )
                }
                IconButton(onClick = { openLink(context, source.url) }) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Open link")
                }
                IconButton(onClick = { copyLink(context, source.url) }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete source?") },
            text = { Text("This will remove the saved link from local storage.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmDelete = false
                        onDelete(source)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
