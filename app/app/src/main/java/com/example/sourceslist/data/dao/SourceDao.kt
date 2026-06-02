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

    @Query("SELECT COUNT(*) FROM sources WHERE url = :url")
    suspend fun duplicateCount(url: String): Int

    @Query("SELECT * FROM sources WHERE sourceId = :sourceId")
    suspend fun getSourceById(sourceId: Long): SourceEntity?

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 0 AND priorityRank IS NOT NULL
        ORDER BY priorityRank ASC
        """
    )
    suspend fun prioritizedSourcesByBracket(bracket: BracketType): List<SourceEntity>

    @Query(
        """
        SELECT * FROM sources
        WHERE bracket = :bracket AND isDone = 0 AND priorityRank = :priorityRank
        LIMIT 1
        """
    )
    suspend fun getSourceByBracketAndPriority(
        bracket: BracketType,
        priorityRank: Int
    ): SourceEntity?

    @Query(
        """
        UPDATE sources
        SET priorityRank = priorityRank - 1, updatedAt = :updatedAt
        WHERE bracket = :bracket AND isDone = 0 AND priorityRank > :removedRank
        """
    )
    suspend fun compactPriorities(
        bracket: BracketType,
        removedRank: Int,
        updatedAt: Long
    )

    @Insert
    suspend fun insert(source: SourceEntity): Long

    @Update
    suspend fun update(source: SourceEntity)

    @Delete
    suspend fun delete(source: SourceEntity)
}
