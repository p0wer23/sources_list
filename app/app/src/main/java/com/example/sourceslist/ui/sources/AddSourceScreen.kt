package com.example.sourceslist.ui.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourceScreen(
    onBack: () -> Unit,
    urlError: String?,
    pendingDuplicate: PendingDuplicate?,
    onUrlChanged: () -> Unit,
    onSave: (url: String, title: String?) -> Unit,
    onConfirmDuplicate: () -> Unit,
    onDismissDuplicate: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add source") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    if (urlError != null) {
                        onUrlChanged()
                    }
                },
                label = { Text("URL") },
                singleLine = true,
                isError = urlError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                supportingText = {
                    if (urlError != null) {
                        Text(urlError)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title or label") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onSave(url, title) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to Unclassified")
            }
        }
    }

    if (pendingDuplicate != null) {
        AlertDialog(
            onDismissRequest = onDismissDuplicate,
            title = { Text("Duplicate link") },
            text = { Text("This URL is already saved. Add it again?") },
            confirmButton = {
                TextButton(onClick = onConfirmDuplicate) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDuplicate) {
                    Text("Cancel")
                }
            }
        )
    }
}
