package com.example.sourceslist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sourceslist.data.SeriousGroupSummary
import com.example.sourceslist.data.entity.SeriousGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriousGroupDao {
    @Query(
        """
        SELECT
            sg.groupId AS groupId,
            sg.name AS name,
            sg.groupPriorityRank AS groupPriorityRank,
            sg.isBuiltIn AS isBuiltIn,
            sg.createdAt AS createdAt,
            sg.updatedAt AS updatedAt,
            COALESCE(SUM(CASE WHEN s.isDone = 0 THEN 1 ELSE 0 END), 0) AS activeCount,
            COALESCE(SUM(CASE WHEN s.isDone = 1 THEN 1 ELSE 0 END), 0) AS completedCount
        FROM serious_groups sg
        LEFT JOIN sources s
            ON COALESCE(s.seriousGroupId, 1) = sg.groupId
            AND s.bracket = 'SERIOUS'
        GROUP BY sg.groupId
        ORDER BY
            CASE WHEN sg.groupPriorityRank IS NULL THEN 1 ELSE 0 END ASC,
            sg.groupPriorityRank ASC,
            sg.createdAt ASC
        """
    )
    fun observeSummaries(): Flow<List<SeriousGroupSummary>>

    @Query(
        """
        SELECT * FROM serious_groups
        ORDER BY
            CASE WHEN groupPriorityRank IS NULL THEN 1 ELSE 0 END ASC,
            groupPriorityRank ASC,
            createdAt ASC
        """
    )
    fun observeGroups(): Flow<List<SeriousGroupEntity>>

    @Query("SELECT * FROM serious_groups WHERE groupId = :groupId LIMIT 1")
    fun observeGroup(groupId: Long): Flow<SeriousGroupEntity?>

    @Query("SELECT * FROM serious_groups WHERE groupId = :groupId LIMIT 1")
    suspend fun getGroupById(groupId: Long): SeriousGroupEntity?

    @Query("SELECT * FROM serious_groups WHERE normalizedName = :normalizedName LIMIT 1")
    suspend fun getGroupByNormalizedName(normalizedName: String): SeriousGroupEntity?

    @Query(
        """
        SELECT * FROM serious_groups
        WHERE groupPriorityRank = :priorityRank
        LIMIT 1
        """
    )
    suspend fun getGroupByPriority(priorityRank: Int): SeriousGroupEntity?

    @Query(
        """
        UPDATE serious_groups
        SET groupPriorityRank = groupPriorityRank - 1, updatedAt = :updatedAt
        WHERE groupPriorityRank > :removedRank
        """
    )
    suspend fun compactPriorities(
        removedRank: Int,
        updatedAt: Long
    )

    @Insert
    suspend fun insert(group: SeriousGroupEntity): Long

    @Update
    suspend fun update(group: SeriousGroupEntity)

    @Delete
    suspend fun delete(group: SeriousGroupEntity)
}
