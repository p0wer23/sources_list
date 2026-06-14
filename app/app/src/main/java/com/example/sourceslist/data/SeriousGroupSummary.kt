package com.example.sourceslist.data

data class SeriousGroupSummary(
    val groupId: Long,
    val name: String,
    val groupPriorityRank: Int?,
    val isBuiltIn: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val activeCount: Int,
    val completedCount: Int
)
