package com.example.sourceslist.data

import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class SourceRepository(private val database: AppDatabase) {
    private val sourceDao = database.sourceDao()

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
        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            if (current.isDone) return@withTransaction

            val now = System.currentTimeMillis()
            removePriorityIfNeeded(current, now, compact = true)
            sourceDao.update(
                current.copy(
                    bracket = bracket,
                    priorityRank = null,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun setDone(source: SourceEntity, isDone: Boolean) {
        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            val now = System.currentTimeMillis()

            if (isDone) {
                removePriorityIfNeeded(current, now, compact = true)
            }

            sourceDao.update(
                current.copy(
                    isDone = isDone,
                    priorityRank = null,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun delete(source: SourceEntity) {
        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            val now = System.currentTimeMillis()
            removePriorityIfNeeded(current, now, compact = true)
            sourceDao.delete(current)
        }
    }

    suspend fun setPriority(source: SourceEntity, rank: Int) {
        if (rank !in 1..3) return

        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            if (current.isDone || !current.bracket.supportsPriority() || current.priorityRank == rank) {
                return@withTransaction
            }

            val now = System.currentTimeMillis()
            if (current.priorityRank != null) {
                sourceDao.update(current.copy(priorityRank = null, updatedAt = now))
            }

            val existing = sourceDao.getSourceByBracketAndPriority(current.bracket, rank)
            if (existing != null && existing.sourceId != current.sourceId) {
                sourceDao.update(existing.copy(priorityRank = null, updatedAt = now))
            }

            sourceDao.update(current.copy(priorityRank = rank, updatedAt = now))
        }
    }

    suspend fun clearPriority(source: SourceEntity) {
        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            val now = System.currentTimeMillis()
            removePriorityIfNeeded(current, now, compact = true)
        }
    }

    private suspend fun removePriorityIfNeeded(
        source: SourceEntity,
        updatedAt: Long,
        compact: Boolean
    ) {
        val priorityRank = source.priorityRank ?: return
        if (!source.bracket.supportsPriority()) return

        sourceDao.update(source.copy(priorityRank = null, updatedAt = updatedAt))
        if (compact) {
            sourceDao.compactPriorities(source.bracket, priorityRank, updatedAt)
        }
    }

    private fun BracketType.supportsPriority(): Boolean {
        return this == BracketType.CASUAL || this == BracketType.SERIOUS
    }
}
