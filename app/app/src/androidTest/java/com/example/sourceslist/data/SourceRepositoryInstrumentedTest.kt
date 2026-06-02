package com.example.sourceslist.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sourceslist.data.entity.BracketType
import com.example.sourceslist.data.entity.SourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun moving_prioritized_source_clears_source_bracket_priority() = runBlocking {
        val first = insertSource("https://one.test", BracketType.CASUAL, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.CASUAL, createdAt = 200L)

        repository.setPriority(first, 1)
        repository.setPriority(second, 2)
        repository.moveSource(first, BracketType.SERIOUS)

        assertEquals(1, database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
        val moved = database.sourceDao().getSourceById(first.sourceId)
        assertEquals(BracketType.SERIOUS, moved?.bracket)
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
    fun completed_sources_stay_in_created_order_without_priorities() = runBlocking {
        val first = insertSource("https://one.test", BracketType.SERIOUS, createdAt = 100L)
        val second = insertSource("https://two.test", BracketType.SERIOUS, createdAt = 200L)

        repository.setPriority(second, 1)
        repository.setDone(second, true)
        repository.setDone(first, true)

        val completedIds = repository.completedSources(BracketType.SERIOUS)
            .first()
            .map { it.sourceId }

        assertEquals(listOf(first.sourceId, second.sourceId), completedIds)
        assertNull(database.sourceDao().getSourceById(second.sourceId)?.priorityRank)
    }

    private suspend fun insertSource(
        url: String,
        bracket: BracketType,
        createdAt: Long,
        isDone: Boolean = false
    ): SourceEntity {
        val source = SourceEntity(
            url = url,
            title = url,
            bracket = bracket,
            isDone = isDone,
            createdAt = createdAt,
            updatedAt = createdAt
        )
        val sourceId = database.sourceDao().insert(source)
        return source.copy(sourceId = sourceId)
    }
}
