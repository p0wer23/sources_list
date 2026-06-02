package com.example.sourceslist.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sourceslist.data.SourceRepository
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import java.net.URI
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
    private val _urlError = MutableStateFlow<String?>(null)
    val urlError: StateFlow<String?> = _urlError.asStateFlow()

    fun activeSources(bracket: BracketType): Flow<List<SourceEntity>> =
        repository.activeSources(bracket)

    fun completedSources(bracket: BracketType): Flow<List<SourceEntity>> =
        repository.completedSources(bracket)

    fun addSource(url: String, title: String?, onAdded: () -> Unit = {}) {
        val trimmedUrl = url.trim()
        val validationError = validateUrl(trimmedUrl)
        if (validationError != null) {
            _urlError.value = validationError
            return
        }

        _urlError.value = null

        viewModelScope.launch {
            if (repository.isDuplicate(trimmedUrl)) {
                _pendingDuplicate.value = PendingDuplicate(trimmedUrl, title)
            } else {
                repository.addSource(trimmedUrl, title)
                onAdded()
            }
        }
    }

    fun addDuplicateAnyway(onAdded: () -> Unit = {}) {
        val duplicate = _pendingDuplicate.value ?: return
        _pendingDuplicate.value = null
        viewModelScope.launch {
            repository.addSource(duplicate.url, duplicate.title)
            onAdded()
        }
    }

    fun dismissDuplicateWarning() {
        _pendingDuplicate.value = null
    }

    fun clearUrlError() {
        _urlError.value = null
    }

    fun resetAddSourceState() {
        _urlError.value = null
        _pendingDuplicate.value = null
    }

    fun moveSource(source: SourceEntity, bracket: BracketType) {
        if (source.isDone) return

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

    fun setPriority(source: SourceEntity, rank: Int) {
        viewModelScope.launch {
            repository.setPriority(source, rank)
        }
    }

    fun clearPriority(source: SourceEntity) {
        viewModelScope.launch {
            repository.clearPriority(source)
        }
    }

    private fun validateUrl(url: String): String? {
        if (url.isBlank()) return "Enter a URL."

        val uri = runCatching { URI(url) }.getOrNull()
        val scheme = uri?.scheme?.lowercase()
        val host = uri?.host
        return if (scheme in setOf("http", "https") && !host.isNullOrBlank()) {
            null
        } else {
            "Enter a valid http:// or https:// URL."
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
