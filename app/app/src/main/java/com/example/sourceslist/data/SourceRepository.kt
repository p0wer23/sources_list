package com.example.sourceslist.data

import com.example.sourceslist.data.dao.SourceDao
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

class SourceRepository(private val sourceDao: SourceDao) {
    fun activeSources(bracket: BracketType): Flow<List<SourceEntity>> =
        sourceDao.activeSourcesByBracket(bracket)

    fun completedSources(bracket: BracketType): Flow<List<SourceEntity>> =
        sourceDao.completedSourcesByBracket(bracket)

    suspend fun isDuplicate(url: String): Boolean =
        sourceDao.duplicateCount(url) > 0

    suspend fun addSource(url: String, title: String?) {
        val now = System.currentTimeMillis()
        sourceDao.insert(
            SourceEntity(
                url = url,
                title = title?.trim()?.takeIf { it.isNotBlank() },
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun moveSource(source: SourceEntity, bracket: BracketType) {
        if (source.isDone) return

        sourceDao.update(
            source.copy(
                bracket = bracket,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun setDone(source: SourceEntity, isDone: Boolean) {
        sourceDao.update(source.copy(isDone = isDone, updatedAt = System.currentTimeMillis()))
    }

    suspend fun delete(source: SourceEntity) {
        sourceDao.delete(source)
    }
}
