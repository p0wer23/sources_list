package com.example.sourceslist.ui.sources

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sourceslist.data.SeriousGroupSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriousGroupsScreen(
    viewModel: SourceViewModel,
    onBack: () -> Unit,
    onOpenGroup: (Long) -> Unit
) {
    val groups by viewModel.seriousGroupSummaries().collectAsState(initial = emptyList())
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("Serious") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add group")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (groups.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No Serious groups",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(groups, key = { it.groupId }) { group ->
                    SeriousGroupCard(
                        group = group,
                        onOpen = { onOpenGroup(group.groupId) },
                        onSetPriority = viewModel::setSeriousGroupPriority,
                        onClearPriority = viewModel::clearSeriousGroupPriority,
                        onRename = viewModel::renameSeriousGroup,
                        onDelete = viewModel::deleteSeriousGroup
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        SeriousGroupNameDialog(
            title = "Add group",
            confirmLabel = "Add",
            initialValue = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, setError, close ->
                viewModel.addSeriousGroup(
                    name = name,
                    onSuccess = {
                        close()
                        showAddDialog = false
                    },
                    onError = setError
                )
            }
        )
    }
}

@Composable
private fun SeriousGroupCard(
    group: SeriousGroupSummary,
    onOpen: () -> Unit,
    onSetPriority: (Long, Int) -> Unit,
    onClearPriority: (Long) -> Unit,
    onRename: (Long, String, () -> Unit, (String) -> Unit) -> Unit,
    onDelete: (Long) -> Unit
) {
    var actionsExpanded by rememberSaveable(group.groupId) { mutableStateOf(false) }
    var showRenameDialog by rememberSaveable(group.groupId) { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable(group.groupId) { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
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
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${group.activeCount} active • ${group.completedCount} completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (group.groupPriorityRank != null) {
                    ElevatedAssistChip(
                        onClick = {},
                        label = { Text("P${group.groupPriorityRank}") }
                    )
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                    onSetPriority(group.groupId, rank)
                                },
                                enabled = group.groupPriorityRank != rank,
                                label = { Text("P$rank") }
                            )
                        }
                        if (group.groupPriorityRank != null) {
                            AssistChip(
                                onClick = {
                                    actionsExpanded = false
                                    onClearPriority(group.groupId)
                                },
                                label = { Text("Clear") }
                            )
                        }
                    }

                    if (!group.isBuiltIn) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = {
                                    actionsExpanded = false
                                    showRenameDialog = true
                                },
                                label = { Text("Rename") },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            AssistChip(
                                onClick = {
                                    actionsExpanded = false
                                    showDeleteDialog = true
                                },
                                label = { Text("Delete") },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showRenameDialog) {
        SeriousGroupNameDialog(
            title = "Rename group",
            confirmLabel = "Save",
            initialValue = group.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { name, setError, close ->
                onRename(
                    group.groupId,
                    name,
                    {
                        close()
                        showRenameDialog = false
                    },
                    setError
                )
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete group?") },
            text = {
                Text("All links in this group will move to Unclassified as active links.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(group.groupId)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SeriousGroupNameDialog(
    title: String,
    confirmLabel: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        setError: (String) -> Unit,
        close: () -> Unit
    ) -> Unit
) {
    var value by rememberSaveable(title, initialValue) { mutableStateOf(initialValue) }
    var error by rememberSaveable(title, initialValue) { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        value = it
                        error = null
                    },
                    label = { Text("Group name") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        value,
                        { message -> error = message },
                        onDismiss
                    )
                }
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
