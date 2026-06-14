package com.example.sourceslist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 0
        ORDER BY
            CASE WHEN priorityRank IS NULL THEN 1 ELSE 0 END ASC,
            priorityRank ASC,
            createdAt ASC
        """
    )
    fun activeSourcesByBracket(bracket: BracketType): Flow<List<SourceEntity>>

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 1
        ORDER BY createdAt ASC
        """
    )
    fun completedSourcesByBracket(bracket: BracketType): Flow<List<SourceEntity>>

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 0 AND COALESCE(seriousGroupId, 1) = :seriousGroupId
        ORDER BY
            CASE WHEN priorityRank IS NULL THEN 1 ELSE 0 END ASC,
            priorityRank ASC,
            createdAt ASC
        """
    )
    fun activeSourcesBySeriousGroup(
        bracket: BracketType,
        seriousGroupId: Long
    ): Flow<List<SourceEntity>>

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 1 AND COALESCE(seriousGroupId, 1) = :seriousGroupId
        ORDER BY createdAt ASC
        """
    )
    fun completedSourcesBySeriousGroup(
        bracket: BracketType,
        seriousGroupId: Long
    ): Flow<List<SourceEntity>>

    @Query("SELECT COUNT(*) FROM sources WHERE url = :url")
    suspend fun duplicateCount(url: String): Int

    @Query("SELECT * FROM sources WHERE sourceId = :sourceId")
    suspend fun getSourceById(sourceId: Long): SourceEntity?

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket
            AND isDone = 0
            AND priorityRank IS NOT NULL
            AND (
                (:seriousGroupId IS NULL AND seriousGroupId IS NULL)
                OR COALESCE(seriousGroupId, 1) = :seriousGroupId
            )
        ORDER BY priorityRank ASC
        """
    )
    suspend fun prioritizedSourcesByScope(
        bracket: BracketType,
        seriousGroupId: Long?
    ): List<SourceEntity>

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket
            AND isDone = 0
            AND priorityRank = :priorityRank
            AND (
                (:seriousGroupId IS NULL AND seriousGroupId IS NULL)
                OR COALESCE(seriousGroupId, 1) = :seriousGroupId
            )
        LIMIT 1
        """
    )
    suspend fun getSourceByScopeAndPriority(
        bracket: BracketType,
        seriousGroupId: Long?,
        priorityRank: Int
    ): SourceEntity?

    @Query(
        """
        UPDATE sources
        SET priorityRank = priorityRank - 1, updatedAt = :updatedAt
        WHERE bracket = :bracket
            AND isDone = 0
            AND priorityRank > :removedRank
            AND (
                (:seriousGroupId IS NULL AND seriousGroupId IS NULL)
                OR COALESCE(seriousGroupId, 1) = :seriousGroupId
            )
        """
    )
    suspend fun compactPriorities(
        bracket: BracketType,
        seriousGroupId: Long?,
        removedRank: Int,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE sources
        SET bracket = :bracket,
            seriousGroupId = NULL,
            priorityRank = NULL,
            isDone = 0,
            updatedAt = :updatedAt
        WHERE bracket = 'SERIOUS' AND seriousGroupId = :groupId
        """
    )
    suspend fun moveSeriousGroupSourcesToBracket(
        groupId: Long,
        bracket: BracketType,
        updatedAt: Long
    )

    @Insert
    suspend fun insert(source: SourceEntity): Long

    @Update
    suspend fun update(source: SourceEntity)

    @Delete
    suspend fun delete(source: SourceEntity)
}
