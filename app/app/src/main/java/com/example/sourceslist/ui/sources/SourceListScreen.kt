package com.example.sourceslist.ui.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
                    onDelete = viewModel::delete,
                    onSetPriority = viewModel::setPriority,
                    onClearPriority = viewModel::clearPriority
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
    onDelete: (SourceEntity) -> Unit,
    onSetPriority: (SourceEntity, Int) -> Unit,
    onClearPriority: (SourceEntity) -> Unit
) {
    val context = LocalContext.current
    var actionsExpanded by rememberSaveable(source.sourceId) { mutableStateOf(false) }
    var confirmDelete by rememberSaveable(source.sourceId) { mutableStateOf(false) }
    val displayTitle = source.title?.takeIf { it.isNotBlank() } ?: source.url
    val canMove = !source.isDone
    val canPrioritize = !showCompleted && !source.isDone && source.bracket != BracketType.UNCLASSIFIED
    val moveTargets = BracketType.entries.filter { it != source.bracket }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
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
                TextButton(onClick = { actionsExpanded = !actionsExpanded }) {
                    Text(if (actionsExpanded) "Close" else "Actions")
                    Icon(
                        imageVector = if (actionsExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = null
                    )
                }
            }

            if (actionsExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (canMove && moveTargets.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            itemVerticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Move to:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            moveTargets.forEach { bracket ->
                                AssistChip(
                                    onClick = {
                                        actionsExpanded = false
                                        onMove(source, bracket)
                                    },
                                    label = { Text(bracket.label) }
                                )
                            }
                        }
                    }

                    if (canPrioritize) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            itemVerticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Priority:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            (1..3).forEach { rank ->
                                AssistChip(
                                    onClick = {
                                        actionsExpanded = false
                                        onSetPriority(source, rank)
                                    },
                                    enabled = source.priorityRank != rank,
                                    label = { Text("Set P$rank") }
                                )
                            }
                            if (source.priorityRank != null) {
                                AssistChip(
                                    onClick = {
                                        actionsExpanded = false
                                        onClearPriority(source)
                                    },
                                    label = { Text("Clear priority") }
                                )
                            }
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!showCompleted && source.bracket != BracketType.UNCLASSIFIED) {
                            ElevatedAssistChip(
                                onClick = {
                                    actionsExpanded = false
                                    onDone(source)
                                },
                                label = { Text("Mark done") },
                                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) }
                            )
                        }
                        if (showCompleted) {
                            AssistChip(
                                onClick = {
                                    actionsExpanded = false
                                    onRestore(source)
                                },
                                label = { Text("Restore") }
                            )
                        }
                        AssistChip(
                            onClick = {
                                actionsExpanded = false
                                confirmDelete = true
                            },
                            label = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
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
                if (!source.isDone && source.priorityRank != null) {
                    ElevatedAssistChip(
                        onClick = {},
                        label = { Text("P${source.priorityRank}") }
                    )
                }
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
