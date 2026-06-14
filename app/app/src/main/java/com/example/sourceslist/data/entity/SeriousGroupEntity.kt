package com.example.sourceslist.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(
    tableName = "serious_groups",
    indices = [
        Index(value = ["normalizedName"], unique = true)
    ]
)
data class SeriousGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val name: String,
    val normalizedName: String,
    val groupPriorityRank: Int? = null,
    val isBuiltIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val UNGROUPED_GROUP_ID: Long = 1L
        const val UNGROUPED_GROUP_NAME: String = "Ungrouped"

        fun normalizedName(name: String): String = name.trim().lowercase(Locale.ROOT)
    }
}
