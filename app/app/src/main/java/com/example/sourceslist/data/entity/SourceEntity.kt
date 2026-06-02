package com.example.sourceslist.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sources",
    indices = [
        Index(value = ["bracket", "priorityRank"], unique = true)
    ]
)
data class SourceEntity(
    @PrimaryKey(autoGenerate = true)
    val sourceId: Long = 0,
    val url: String,
    val title: String?,
    val bracket: BracketType = BracketType.UNCLASSIFIED,
    val priorityRank: Int? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
