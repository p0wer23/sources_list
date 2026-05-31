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
        ORDER BY createdAt ASC
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

    @Insert
    suspend fun insert(source: SourceEntity)

    @Update
    suspend fun update(source: SourceEntity)

    @Delete
    suspend fun delete(source: SourceEntity)
}
