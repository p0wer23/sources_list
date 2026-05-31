package com.example.sourceslist.ui.sources

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sourceslist.data.entity.BracketType

private data class HomeBracketSelector(
    val bracket: BracketType,
    val description: String
)

private object SourcesRoute {
    const val HOME = "home"
    const val ADD = "add"
    const val BRACKET = "bracket/{bracket}"

    fun bracket(bracket: BracketType): String = "bracket/${bracket.name}"
}

private val homeSelectors = listOf(
    HomeBracketSelector(
        bracket = BracketType.UNCLASSIFIED,
        description = "New links waiting for a decision."
    ),
    HomeBracketSelector(
        bracket = BracketType.CASUAL,
        description = "Things you can read or watch whenever."
    ),
    HomeBracketSelector(
        bracket = BracketType.SERIOUS,
        description = "Items that need focused time."
    )
)

@Composable
fun SourcesApp(viewModel: SourceViewModel) {
    val navController = rememberNavController()
    val pendingDuplicate by viewModel.pendingDuplicate.collectAsState()

    NavHost(
        navController = navController,
        startDestination = SourcesRoute.HOME
    ) {
        composable(SourcesRoute.HOME) {
            HomeScreen(
                onAddSource = { navController.navigate(SourcesRoute.ADD) },
                onOpenBracket = { bracket ->
                    navController.navigate(SourcesRoute.bracket(bracket))
                }
            )
        }
        composable(SourcesRoute.ADD) {
            AddSourceScreen(
                onBack = { navController.popBackStack() },
                onSave = { url, title ->
                    viewModel.addSource(url = url, title = title)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = SourcesRoute.BRACKET,
            arguments = listOf(navArgument("bracket") { type = NavType.StringType })
        ) { backStackEntry ->
            val bracketName = backStackEntry.arguments?.getString("bracket")
            val bracket = BracketType.entries.firstOrNull { it.name == bracketName }
                ?: BracketType.UNCLASSIFIED

            BracketScreen(
                viewModel = viewModel,
                bracket = bracket,
                onBack = { navController.popBackStack() }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    onAddSource: () -> Unit,
    onOpenBracket: (BracketType) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("Sources List") },
                actions = {
                    IconButton(onClick = onAddSource) {
                        Icon(Icons.Default.Add, contentDescription = "Add URL")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Choose a bracket",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Home only routes you to the right list. URLs stay inside each bracket page.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            homeSelectors.forEach { selector ->
                BracketSelectorCard(
                    bracket = selector.bracket,
                    description = selector.description,
                    onClick = { onOpenBracket(selector.bracket) }
                )
            }
        }
    }
}

@Composable
private fun BracketSelectorCard(
    bracket: BracketType,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = bracket.label,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = ">",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BracketScreen(
    viewModel: SourceViewModel,
    bracket: BracketType,
    onBack: () -> Unit
) {
    var showCompleted by rememberSaveable(bracket) { mutableStateOf(false) }
    val supportsCompleted = bracket != BracketType.UNCLASSIFIED

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text(bracket.label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (supportsCompleted) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !showCompleted,
                        onClick = { showCompleted = false },
                        label = { Text("Active") }
                    )
                    FilterChip(
                        selected = showCompleted,
                        onClick = { showCompleted = true },
                        label = { Text("Completed") }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                SourceListScreen(
                    viewModel = viewModel,
                    bracket = bracket,
                    showCompleted = showCompleted
                )
            }
        }
    }
}
