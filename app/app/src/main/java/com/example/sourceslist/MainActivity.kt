package com.example.sourceslist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sourceslist.data.AppDatabase
import com.example.sourceslist.data.SourceRepository
import com.example.sourceslist.ui.sources.SourceViewModel
import com.example.sourceslist.ui.sources.SourceViewModelFactory
import com.example.sourceslist.ui.sources.SourcesApp
import com.example.sourceslist.ui.theme.SourcesListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SourcesListTheme {
                val database = AppDatabase.getDatabase(applicationContext)
                val viewModel: SourceViewModel = viewModel(
                    factory = SourceViewModelFactory(SourceRepository(database.sourceDao()))
                )
                SourcesApp(viewModel = viewModel)
            }
        }
    }
}
