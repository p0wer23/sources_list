package com.example.sourceslist.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SeriousGroupEntity
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SourceRepositoryInstrumentedTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: SourceRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(AppDatabase.DEFAULT_SERIOUS_GROUP_CALLBACK)
            .allowMainThreadQueries()
            .build()
        repository = SourceRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun active_sources_are_sorted_by_priority_then_created_time() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)
        val third = insertSource("https://three.test", BracketType.CASUAL, createdAt = 300L)

        repository.setPriority(second, 2)
        repository.setPriority(third, 1)

        val orderedIds = repository.activeSources(BracketType.CASUAL)
            .first()
            .map { it.sourceId }

        assertEquals(listOf(third.sourceId, second.sourceId, first.sourceId), orderedIds)
    }

    @Test
    fun setting_occupied_rank_replaces_old_holder() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 1)

        assertNull(database.sourceDao().getSourceById(first.sourceId)?.priorityRank)
        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    @Test
    fun clearing_priority_compacts_higher_ranks() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)
        val third = insertSource("https://three.test", BracketType.CASUAL, createdAt = 300L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.setPriority(third, 3)

        repository.clearPriority(first)

        assertNull(database.sourceDao().getSourceById(first.sourceId)?.priorityRank)
        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
        assertEquals(2, database.sourceDao().getSourceById(third.sourceId)?.priorityRank)
    }

    @Test
    fun marking_prioritized_source_done_clears_and_compacts() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.setDone(first, true)

        val completed = repository.completedSources(BracketType.CASUAL).first().single()
        assertEquals(first.sourceId, completed.sourceId)
        assertNull(completed.priorityRank)
        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    @Test
    fun deleting_prioritized_source_compacts_remaining_ranks() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.delete(first)

        assertNull(database.sourceDao().getSourceById(first.sourceId))
        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    @Test
    fun moving_prioritized_source_clears_old_scope_priority() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.moveSource(
            first,
            BracketType.SERIOUS,
            SeriousGroupEntity.UNGROUPED_GROUP_ID
        )

        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
        val moved = database.sourceDao().getSourceById(first.sourceId)
        assertEquals(BracketType.SERIOUS, moved?.bracket)
        assertEquals(SeriousGroupEntity.UNGROUPED_GROUP_ID, moved?.seriousGroupId)
        assertNull(moved?.priorityRank)
    }

    @Test
    fun restoring_done_source_keeps_priority_empty() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)

        repository.setPriority(first, 1)
        repository.setDone(first, true)
        repository.setDone(first, false)

        val restored = database.sourceDao().getSourceById(first.sourceId)
        assertEquals(false, restored?.isDone)
        assertNull(restored?.priorityRank)
    }

    @Test
    fun unclassified_sources_ignore_priority_requests() = runBlocking {
        val source = insertSource("https://one.test", BracketType.UNCLASSIFIED, createdAt = 100L)

        repository.setPriority(source, 1)

        assertNull(database.sourceDao().getSourceById(source.sourceId)?.priorityRank)
    }

    @Test
    fun completed_serious_sources_stay_grouped() = runBlocking {
        val deepWork = insertGroup("Deep Work", createdAt = 100L)
        val first = insertSource(
            "https://one.test",
            BracketType.SERIOUS,
            createdAt = 100L,
            seriousGroupId = deepWork.groupId
        )
        val second = insertSource(
            "https://two.test",
            BracketType.SERIOUS,
            createdAt = 200L,
            seriousGroupId = deepWork.groupId
        )

        repository.setPriority(second, 1)
        repository.setDone(second, true)
        repository.setDone(first, true)

        val completedIds = repository.completedSources(BracketType.SERIOUS, deepWork.groupId)
            .first()
            .map { it.sourceId }

        assertEquals(listOf(first.sourceId, second.sourceId), completedIds)
        assertEquals(deepWork.groupId, database.sourceDao().getSourceById(second.sourceId)?.seriousGroupId)
        assertNull(database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    @Test
    fun serious_group_summaries_are_sorted_by_priority_then_created_time() = runBlocking {
        val first = insertGroup("Writing", createdAt = 100L)
        val second = insertGroup("Research", createdAt = 200L)
        val third = insertGroup("Planning", createdAt = 300L)

        repository.setSeriousGroupPriority(second.groupId, 2)
        repository.setSeriousGroupPriority(third.groupId, 1)

        val orderedIds = repository.seriousGroupSummaries()
            .first()
            .filterNot { it.groupId == SeriousGroupEntity.UNGROUPED_GROUP_ID }
            .map { it.groupId }

        assertEquals(listOf(third.groupId, second.groupId, first.groupId), orderedIds)
    }

    @Test
    fun setting_occupied_group_rank_replaces_old_holder() = runBlocking {
        val first = insertGroup("Writing", createdAt = 100L)
        val second = insertGroup("Research", createdAt = 200L)

        repository.setSeriousGroupPriority(first.groupId, 1)
        repository.setSeriousGroupPriority(second.groupId, 1)

        assertNull(database.seriousGroupDao().getGroupById(first.groupId)?.groupPriorityRank)
        assertEquals(1, database.seriousGroupDao().getGroupById(second.groupId)?.groupPriorityRank)
    }

    @Test
    fun clearing_group_priority_compacts_higher_ranks() = runBlocking {
        val first = insertGroup("Writing", createdAt = 100L)
        val second = insertGroup("Research", createdAt = 200L)
        val third = insertGroup("Planning", createdAt = 300L)

        repository.setSeriousGroupPriority(first.groupId, 1)
        repository.setSeriousGroupPriority(second.groupId, 2)
        repository.setSeriousGroupPriority(third.groupId, 3)

        repository.clearSeriousGroupPriority(first.groupId)

        assertNull(database.seriousGroupDao().getGroupById(first.groupId)?.groupPriorityRank)
        assertEquals(1, database.seriousGroupDao().getGroupById(second.groupId)?.groupPriorityRank)
        assertEquals(2, database.seriousGroupDao().getGroupById(third.groupId)?.groupPriorityRank)
    }

    @Test
    fun different_serious_groups_can_hold_the_same_link_priority() = runBlocking {
        val firstGroup = insertGroup("Writing", createdAt = 100L)
        val secondGroup = insertGroup("Research", createdAt = 200L)
        val first = insertSource(
            "https://one.test",
            BracketType.SERIOUS,
            createdAt = 100L,
            seriousGroupId = firstGroup.groupId
        )
        val second = insertSource(
            "https://two.test",
            BracketType.SERIOUS,
            createdAt = 200L,
            seriousGroupId = secondGroup.groupId
        )

        repository.setPriority(first, 1)
        repository.setPriority(second, 1)

        assertEquals(1, database.sourceDao().getSourceById(first.sourceId)?.priorityRank)
        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    @Test
    fun moving_serious_source_between_groups_clears_old_priority_scope() = runBlocking {
        val firstGroup = insertGroup("Writing", createdAt = 100L)
        val secondGroup = insertGroup("Research", createdAt = 200L)
        val first = insertSource(
            "https://one.test",
            BracketType.SERIOUS,
            createdAt = 100L,
            seriousGroupId = firstGroup.groupId
        )
        val second = insertSource(
            "https://two.test",
            BracketType.SERIOUS,
            createdAt = 200L,
            seriousGroupId = firstGroup.groupId
        )

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.moveSource(first, BracketType.SERIOUS, secondGroup.groupId)

        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
        val moved = database.sourceDao().getSourceById(first.sourceId)
        assertEquals(secondGroup.groupId, moved?.seriousGroupId)
        assertNull(moved?.priorityRank)
    }

    @Test
    fun moving_source_into_serious_assigns_selected_group() = runBlocking {
        val group = insertGroup("Writing", createdAt = 100L)
        val source = insertSource("https://one.test", BracketType.UNCLASSIFIED, createdAt = 100L)

        repository.moveSource(source, BracketType.SERIOUS, group.groupId)

        val moved = database.sourceDao().getSourceById(source.sourceId)
        assertEquals(BracketType.SERIOUS, moved?.bracket)
        assertEquals(group.groupId, moved?.seriousGroupId)
    }

    @Test
    fun deleting_custom_group_moves_sources_to_unclassified_as_active() = runBlocking {
        val group = insertGroup("Writing", createdAt = 100L)
        val active = insertSource(
            "https://one.test",
            BracketType.SERIOUS,
            createdAt = 100L,
            seriousGroupId = group.groupId
        )
        val completed = insertSource(
            "https://two.test",
            BracketType.SERIOUS,
            createdAt = 200L,
            seriousGroupId = group.groupId,
            isDone = true
        )

        repository.deleteSeriousGroup(group.groupId)

        val activeMoved = database.sourceDao().getSourceById(active.sourceId)
        val completedMoved = database.sourceDao().getSourceById(completed.sourceId)
        assertEquals(BracketType.UNCLASSIFIED, activeMoved?.bracket)
        assertEquals(BracketType.UNCLASSIFIED, completedMoved?.bracket)
        assertFalse(activeMoved?.isDone ?: true)
        assertFalse(completedMoved?.isDone ?: true)
        assertNull(activeMoved?.seriousGroupId)
        assertNull(completedMoved?.seriousGroupId)
        assertNull(database.seriousGroupDao().getGroupById(group.groupId))
    }

    @Test
    fun ungrouped_group_cannot_be_deleted() = runBlocking {
        val deleted = repository.deleteSeriousGroup(SeriousGroupEntity.UNGROUPED_GROUP_ID)

        assertFalse(deleted)
        assertTrue(
            database.seriousGroupDao()
                .getGroupById(SeriousGroupEntity.UNGROUPED_GROUP_ID) != null
        )
    }

    private suspend fun insertSource(
        url: String,
        bracket: BracketType,
        createdAt: Long,
        seriousGroupId: Long? = defaultSeriousGroupIdFor(bracket),
        isDone: Boolean = false
    ): SourceEntity {
        val source = SourceEntity(
            url = url,
            title = url,
            bracket = bracket,
            seriousGroupId = seriousGroupId,
            isDone = isDone,
            createdAt = createdAt,
            updatedAt = createdAt
        )
        val sourceId = database.sourceDao().insert(source)
        return source.copy(sourceId = sourceId)
    }

    private suspend fun insertGroup(
        name: String,
        createdAt: Long
    ): SeriousGroupEntity {
        val group = SeriousGroupEntity(
            name = name,
            normalizedName = SeriousGroupEntity.normalizedName(name),
            createdAt = createdAt,
            updatedAt = createdAt
        )
        val groupId = database.seriousGroupDao().insert(group)
        return group.copy(groupId = groupId)
    }

    private fun defaultSeriousGroupIdFor(bracket: BracketType): Long? {
        return if (bracket == BracketType.SERIOUS) {
            SeriousGroupEntity.UNGROUPED_GROUP_ID
        } else {
            null
        }
    }
}
