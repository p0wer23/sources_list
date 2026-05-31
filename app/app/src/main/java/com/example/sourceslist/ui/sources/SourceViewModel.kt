package com.example.sourceslist.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sourceslist.data.SourceRepository
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PendingDuplicate(
    val url: String,
    val title: String?
)

class SourceViewModel(private val repository: SourceRepository) : ViewModel() {
    private val _pendingDuplicate = MutableStateFlow<PendingDuplicate?>(null)
    val pendingDuplicate: StateFlow<PendingDuplicate?> = _pendingDuplicate.asStateFlow()

    fun activeSources(bracket: BracketType): Flow<List<SourceEntity>> =
        repository.activeSources(bracket)

    fun completedSources(bracket: BracketType): Flow<List<SourceEntity>> =
        repository.completedSources(bracket)

    fun addSource(url: String, title: String?) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank()) return

        viewModelScope.launch {
            if (repository.isDuplicate(trimmedUrl)) {
                _pendingDuplicate.value = PendingDuplicate(trimmedUrl, title)
            } else {
                repository.addSource(trimmedUrl, title)
            }
        }
    }

    fun addDuplicateAnyway() {
        val duplicate = _pendingDuplicate.value ?: return
        _pendingDuplicate.value = null
        viewModelScope.launch {
            repository.addSource(duplicate.url, duplicate.title)
        }
    }

    fun dismissDuplicateWarning() {
        _pendingDuplicate.value = null
    }

    fun moveSource(source: SourceEntity, bracket: BracketType) {
        viewModelScope.launch {
            repository.moveSource(source, bracket)
        }
    }

    fun markDone(source: SourceEntity) {
        viewModelScope.launch {
            repository.setDone(source, true)
        }
    }

    fun restore(source: SourceEntity) {
        viewModelScope.launch {
            repository.setDone(source, false)
        }
    }

    fun delete(source: SourceEntity) {
        viewModelScope.launch {
            repository.delete(source)
        }
    }
}

class SourceViewModelFactory(private val repository: SourceRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SourceViewModel::class.java)) {
            return SourceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
