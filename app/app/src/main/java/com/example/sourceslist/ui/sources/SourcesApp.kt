package com.example.sourceslist.ui.sources

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SeriousGroupEntity

private data class HomeBracketSelector(
    val bracket: BracketType
)

private object SourcesRoute {
    const val HOME = "home"
    const val ADD = "add"
    const val SERIOUS_GROUPS = "serious-groups"
    const val SERIOUS_GROUP = "seriousGroup/{groupId}"
    const val BRACKET = "bracket/{bracket}"

    fun bracket(bracket: BracketType): String = "bracket/${bracket.name}"
    fun seriousGroup(groupId: Long): String = "seriousGroup/$groupId"
}

private val homeSelectors = listOf(
    HomeBracketSelector(bracket = BracketType.UNCLASSIFIED),
    HomeBracketSelector(bracket = BracketType.CASUAL),
    HomeBracketSelector(bracket = BracketType.SERIOUS)
)

private val noEnterTransition: EnterTransition = EnterTransition.None
private val noExitTransition: ExitTransition = ExitTransition.None

@Composable
fun SourcesApp(viewModel: SourceViewModel) {
    val navController = rememberNavController()
    val pendingDuplicate by viewModel.pendingDuplicate.collectAsState()
    val urlError by viewModel.urlError.collectAsState()

    NavHost(
        navController = navController,
        startDestination = SourcesRoute.HOME
    ) {
        composable(
            route = SourcesRoute.HOME,
            enterTransition = { noEnterTransition },
            exitTransition = { noExitTransition },
            popEnterTransition = { noEnterTransition },
            popExitTransition = { noExitTransition }
        ) {
            HomeScreen(
                onAddSource = { navController.navigate(SourcesRoute.ADD) },
                onOpenBracket = { bracket ->
                    if (bracket == BracketType.SERIOUS) {
                        navController.navigate(SourcesRoute.SERIOUS_GROUPS)
                    } else {
                        navController.navigate(SourcesRoute.bracket(bracket))
                    }
                }
            )
        }
        composable(
            route = SourcesRoute.ADD,
            enterTransition = { noEnterTransition },
            exitTransition = { noExitTransition },
            popEnterTransition = { noEnterTransition },
            popExitTransition = { noExitTransition }
        ) {
            AddSourceScreen(
                onBack = {
                    viewModel.resetAddSourceState()
                    navController.popBackStack()
                },
                urlError = urlError,
                pendingDuplicate = pendingDuplicate,
                onUrlChanged = viewModel::clearUrlError,
                onSave = { url, title ->
                    viewModel.addSource(
                        url = url,
                        title = title,
                        onAdded = { navController.popBackStack() }
                    )
                },
                onConfirmDuplicate = {
                    viewModel.addDuplicateAnyway(
                        onAdded = { navController.popBackStack() }
                    )
                },
                onDismissDuplicate = viewModel::dismissDuplicateWarning
            )
        }
        composable(
            route = SourcesRoute.SERIOUS_GROUPS,
            enterTransition = { noEnterTransition },
            exitTransition = { noExitTransition },
            popEnterTransition = { noEnterTransition },
            popExitTransition = { noExitTransition }
        ) {
            SeriousGroupsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenGroup = { groupId ->
                    navController.navigate(SourcesRoute.seriousGroup(groupId))
                }
            )
        }
        composable(
            route = SourcesRoute.SERIOUS_GROUP,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType }),
            enterTransition = { noEnterTransition },
            exitTransition = { noExitTransition },
            popEnterTransition = { noEnterTransition },
            popExitTransition = { noExitTransition }
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId")
                ?: SeriousGroupEntity.UNGROUPED_GROUP_ID

            SeriousGroupDetailScreen(
                viewModel = viewModel,
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = SourcesRoute.BRACKET,
            arguments = listOf(navArgument("bracket") { type = NavType.StringType }),
            enterTransition = { noEnterTransition },
            exitTransition = { noExitTransition },
            popEnterTransition = { noEnterTransition },
            popExitTransition = { noExitTransition }
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
            homeSelectors.forEach { selector ->
                BracketSelectorCard(
                    bracket = selector.bracket,
                    onClick = { onOpenBracket(selector.bracket) }
                )
            }
        }
    }
}

@Composable
private fun BracketSelectorCard(
    bracket: BracketType,
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
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriousGroupDetailScreen(
    viewModel: SourceViewModel,
    groupId: Long,
    onBack: () -> Unit
) {
    val group by viewModel.seriousGroup(groupId).collectAsState(initial = null)
    var showCompleted by rememberSaveable(groupId) { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Serious") },
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

            Box(modifier = Modifier.fillMaxSize()) {
                SourceListScreen(
                    viewModel = viewModel,
                    bracket = BracketType.SERIOUS,
                    showCompleted = showCompleted,
                    seriousGroupId = groupId
                )
            }
        }
    }
}
