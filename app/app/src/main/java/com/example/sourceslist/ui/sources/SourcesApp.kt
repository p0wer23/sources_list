package com.example.sourceslist.ui.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import com.example.sourceslist.ui.common.copyLink
import com.example.sourceslist.ui.common.openLink

private data class SourceFilter(
    val label: String,
    val bracket: BracketType,
    val completed: Boolean = false
)

private val filters = listOf(
    SourceFilter("Inbox", BracketType.UNCLASSIFIED),
    SourceFilter("Casual", BracketType.CASUAL),
    SourceFilter("Serious", BracketType.SERIOUS),
    SourceFilter("Done C", BracketType.CASUAL, completed = true),
    SourceFilter("Done S", BracketType.SERIOUS, completed = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesApp(viewModel: SourceViewModel) {
    var selectedFilter by remember { mutableStateOf(filters.first()) }
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val pendingDuplicate by viewModel.pendingDuplicate.collectAsState()
    val sources by if (selectedFilter.completed) {
        viewModel.completedSources(selectedFilter.bracket).collectAsState(initial = emptyList())
    } else {
        viewModel.activeSources(selectedFilter.bracket).collectAsState(initial = emptyList())
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(title = { Text("Sources List") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.addSource(url, title)
                        url = ""
                        title = ""
                        selectedFilter = filters.first()
                    },
                    enabled = url.isNotBlank()
                ) {
                    Text("Add")
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(filters) { _, filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) }
                    )
                }
            }

            if (sources.isEmpty()) {
                Text(
                    text = "No sources",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sources, key = { it.sourceId }) { source ->
                        MinimalSourceRow(
                            source = source,
                            completedView = selectedFilter.completed,
                            onMove = viewModel::moveSource,
                            onDone = viewModel::markDone,
                            onRestore = viewModel::restore,
                            onDelete = viewModel::delete
                        )
                    }
                }
            }
        }
    }

    pendingDuplicate?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissDuplicateWarning,
            title = { Text("Duplicate link") },
            text = { Text("This URL is already saved. Add it again?") },
            confirmButton = {
                TextButton(onClick = viewModel::addDuplicateAnyway) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDuplicateWarning) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MinimalSourceRow(
    source: SourceEntity,
    completedView: Boolean,
    onMove: (SourceEntity, BracketType) -> Unit,
    onDone: (SourceEntity) -> Unit,
    onRestore: (SourceEntity) -> Unit,
    onDelete: (SourceEntity) -> Unit
) {
    val context = LocalContext.current
    val displayTitle = source.title?.takeIf { it.isNotBlank() } ?: source.url

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.titleSmall,
                textDecoration = if (source.isDone) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (source.title?.isNotBlank() == true) {
                Text(
                    text = source.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(onClick = {}, label = { Text(source.bracket.label) })
                IconButton(onClick = { openLink(context, source.url) }) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Open")
                }
                IconButton(onClick = { copyLink(context, source.url) }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                }
                if (source.bracket != BracketType.UNCLASSIFIED) {
                    IconButton(onClick = { if (completedView) onRestore(source) else onDone(source) }) {
                        Icon(Icons.Default.Check, contentDescription = if (completedView) "Restore" else "Done")
                    }
                }
                IconButton(onClick = { onDelete(source) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (source.bracket == BracketType.UNCLASSIFIED) {
                    TextButton(onClick = { onMove(source, BracketType.CASUAL) }) {
                        Text("Casual")
                    }
                    TextButton(onClick = { onMove(source, BracketType.SERIOUS) }) {
                        Text("Serious")
                    }
                } else {
                    TextButton(
                        onClick = {
                            val target = if (source.bracket == BracketType.CASUAL) {
                                BracketType.SERIOUS
                            } else {
                                BracketType.CASUAL
                            }
                            onMove(source, target)
                        }
                    ) {
                        Text(if (source.bracket == BracketType.CASUAL) "Serious" else "Casual")
                    }
                }
            }
        }
    }
}
