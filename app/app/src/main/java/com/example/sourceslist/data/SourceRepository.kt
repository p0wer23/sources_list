package com.example.sourceslist.data

import androidx.room.withTransaction
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SeriousGroupEntity
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

enum class SeriousGroupMutationResult {
    SUCCESS,
    BLANK_NAME,
    DUPLICATE_NAME,
    NOT_ALLOWED
}

class SourceRepository(private val database: AppDatabase) {
    private val sourceDao = database.sourceDao()
    private val seriousGroupDao = database.seriousGroupDao()

    fun activeSources(
        bracket: BracketType,
        seriousGroupId: Long? = null
    ): Flow<List<SourceEntity>> {
        return if (bracket == BracketType.SERIOUS) {
            sourceDao.activeSourcesBySeriousGroup(
                bracket = bracket,
                seriousGroupId = seriousGroupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
            )
        } else {
            sourceDao.activeSourcesByBracket(bracket)
        }
    }

    fun completedSources(
        bracket: BracketType,
        seriousGroupId: Long? = null
    ): Flow<List<SourceEntity>> {
        return if (bracket == BracketType.SERIOUS) {
            sourceDao.completedSourcesBySeriousGroup(
                bracket = bracket,
                seriousGroupId = seriousGroupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
            )
        } else {
            sourceDao.completedSourcesByBracket(bracket)
        }
    }

    fun seriousGroupSummaries(): Flow<List<SeriousGroupSummary>> =
        seriousGroupDao.observeSummaries()

    fun seriousGroups(): Flow<List<SeriousGroupEntity>> =
        seriousGroupDao.observeGroups()

    fun seriousGroup(groupId: Long): Flow<SeriousGroupEntity?> =
        seriousGroupDao.observeGroup(groupId)

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

    suspend fun addSeriousGroup(name: String): SeriousGroupMutationResult {
        val trimmedName = name.trim()
        val normalizedName = SeriousGroupEntity.normalizedName(trimmedName)
        if (trimmedName.isBlank()) {
            return SeriousGroupMutationResult.BLANK_NAME
        }
        if (seriousGroupDao.getGroupByNormalizedName(normalizedName) != null) {
            return SeriousGroupMutationResult.DUPLICATE_NAME
        }

        val now = System.currentTimeMillis()
        seriousGroupDao.insert(
            SeriousGroupEntity(
                name = trimmedName,
                normalizedName = normalizedName,
                isBuiltIn = false,
                createdAt = now,
                updatedAt = now
            )
        )
        return SeriousGroupMutationResult.SUCCESS
    }

    suspend fun renameSeriousGroup(
        groupId: Long,
        name: String
    ): SeriousGroupMutationResult {
        val trimmedName = name.trim()
        val normalizedName = SeriousGroupEntity.normalizedName(trimmedName)
        if (trimmedName.isBlank()) {
            return SeriousGroupMutationResult.BLANK_NAME
        }

        val current = seriousGroupDao.getGroupById(groupId) ?: return SeriousGroupMutationResult.NOT_ALLOWED
        if (current.isBuiltIn) {
            return SeriousGroupMutationResult.NOT_ALLOWED
        }

        val duplicate = seriousGroupDao.getGroupByNormalizedName(normalizedName)
        if (duplicate != null && duplicate.groupId != groupId) {
            return SeriousGroupMutationResult.DUPLICATE_NAME
        }

        val now = System.currentTimeMillis()
        seriousGroupDao.update(
            current.copy(
                name = trimmedName,
                normalizedName = normalizedName,
                updatedAt = now
            )
        )
        return SeriousGroupMutationResult.SUCCESS
    }

    suspend fun deleteSeriousGroup(groupId: Long): Boolean {
        return database.withTransaction {
            val current = seriousGroupDao.getGroupById(groupId) ?: return@withTransaction false
            if (current.isBuiltIn) {
                return@withTransaction false
            }

            val now = System.currentTimeMillis()
            sourceDao.moveSeriousGroupSourcesToBracket(
                groupId = groupId,
                bracket = BracketType.UNCLASSIFIED,
                updatedAt = now
            )
            seriousGroupDao.delete(current)
            current.groupPriorityRank?.let { removedRank ->
                seriousGroupDao.compactPriorities(removedRank = removedRank, updatedAt = now)
            }
            true
        }
    }

    suspend fun setSeriousGroupPriority(groupId: Long, rank: Int) {
        if (rank !in 1..3) return

        database.withTransaction {
            val current = seriousGroupDao.getGroupById(groupId) ?: return@withTransaction
            if (current.groupPriorityRank == rank) return@withTransaction

            val now = System.currentTimeMillis()
            current.groupPriorityRank?.let {
                seriousGroupDao.update(current.copy(groupPriorityRank = null, updatedAt = now))
            }

            val existing = seriousGroupDao.getGroupByPriority(rank)
            if (existing != null && existing.groupId != groupId) {
                seriousGroupDao.update(existing.copy(groupPriorityRank = null, updatedAt = now))
            }

            seriousGroupDao.update(current.copy(groupPriorityRank = rank, updatedAt = now))
        }
    }

    suspend fun clearSeriousGroupPriority(groupId: Long) {
        database.withTransaction {
            val current = seriousGroupDao.getGroupById(groupId) ?: return@withTransaction
            val removedRank = current.groupPriorityRank ?: return@withTransaction
            val now = System.currentTimeMillis()

            seriousGroupDao.update(current.copy(groupPriorityRank = null, updatedAt = now))
            seriousGroupDao.compactPriorities(removedRank = removedRank, updatedAt = now)
        }
    }

    suspend fun moveSource(
        source: SourceEntity,
        bracket: BracketType,
        seriousGroupId: Long? = null
    ) {
        database.withTransaction {
            val current = sourceDao.getSourceById(source.sourceId) ?: return@withTransaction
            if (current.isDone) return@withTransaction

            val targetGroupId = if (bracket == BracketType.SERIOUS) {
                resolveSeriousGroupId(seriousGroupId)
            } else {
                null
            }
            val currentGroupId = current.priorityScopeGroupId()
            if (current.bracket == bracket && currentGroupId == targetGroupId) {
                return@withTransaction
            }

            val now = System.currentTimeMillis()
            removePriorityIfNeeded(current, now, compact = true)
            sourceDao.update(
                current.copy(
                    bracket = bracket,
                    seriousGroupId = targetGroupId,
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
                    seriousGroupId = current.persistedSeriousGroupId(),
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

            val scopeGroupId = current.priorityScopeGroupId()
            val now = System.currentTimeMillis()
            if (current.priorityRank != null) {
                sourceDao.update(current.copy(priorityRank = null, updatedAt = now))
            }

            val existing = sourceDao.getSourceByScopeAndPriority(
                bracket = current.bracket,
                seriousGroupId = scopeGroupId,
                priorityRank = rank
            )
            if (existing != null && existing.sourceId != current.sourceId) {
                sourceDao.update(existing.copy(priorityRank = null, updatedAt = now))
            }

            sourceDao.update(
                current.copy(
                    seriousGroupId = current.persistedSeriousGroupId(),
                    priorityRank = rank,
                    updatedAt = now
                )
            )
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

        sourceDao.update(
            source.copy(
                seriousGroupId = source.persistedSeriousGroupId(),
                priorityRank = null,
                updatedAt = updatedAt
            )
        )
        if (compact) {
            sourceDao.compactPriorities(
                bracket = source.bracket,
                seriousGroupId = source.priorityScopeGroupId(),
                removedRank = priorityRank,
                updatedAt = updatedAt
            )
        }
    }

    private suspend fun resolveSeriousGroupId(groupId: Long?): Long {
        val requestedGroupId = groupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
        val group = seriousGroupDao.getGroupById(requestedGroupId)
        return group?.groupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
    }

    private fun SourceEntity.persistedSeriousGroupId(): Long? {
        return if (bracket == BracketType.SERIOUS) {
            seriousGroupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
        } else {
            null
        }
    }

    private fun SourceEntity.priorityScopeGroupId(): Long? {
        return if (bracket == BracketType.SERIOUS) {
            seriousGroupId ?: SeriousGroupEntity.UNGROUPED_GROUP_ID
        } else {
            null
        }
    }

    private fun BracketType.supportsPriority(): Boolean {
        return this == BracketType.CASUAL || this == BracketType.SERIOUS
    }
}
