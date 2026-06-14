package com.example.sourceslist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sourceslist.data.dao.SeriousGroupDao
import com.example.sourceslist.data.dao.SourceDao
import com.example.sourceslist.data.entity.SeriousGroupEntity
import com.example.sourceslist.data.entity.SourceEntity

@Database(
    entities = [SourceEntity::class, SeriousGroupEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun seriousGroupDao(): SeriousGroupDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE sources ADD COLUMN priorityRank INTEGER"
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_sources_bracket_priorityRank
                    ON sources (bracket, priorityRank)
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `serious_groups` (
                        `groupId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `normalizedName` TEXT NOT NULL,
                        `groupPriorityRank` INTEGER,
                        `isBuiltIn` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_serious_groups_normalizedName`
                    ON `serious_groups` (`normalizedName`)
                    """.trimIndent()
                )
                db.execSQL(
                    "ALTER TABLE sources ADD COLUMN seriousGroupId INTEGER"
                )
                db.execSQL("DROP INDEX IF EXISTS `index_sources_bracket_priorityRank`")
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_sources_bracket_isDone`
                    ON `sources` (`bracket`, `isDone`)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_sources_seriousGroupId`
                    ON `sources` (`seriousGroupId`)
                    """.trimIndent()
                )

                val now = System.currentTimeMillis()
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO serious_groups
                    (groupId, name, normalizedName, groupPriorityRank, isBuiltIn, createdAt, updatedAt)
                    VALUES (${SeriousGroupEntity.UNGROUPED_GROUP_ID}, '${SeriousGroupEntity.UNGROUPED_GROUP_NAME}', 'ungrouped', NULL, 1, $now, $now)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE sources
                    SET seriousGroupId = ${SeriousGroupEntity.UNGROUPED_GROUP_ID}
                    WHERE bracket = 'SERIOUS'
                    """.trimIndent()
                )
            }
        }

        val DEFAULT_SERIOUS_GROUP_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                insertUngroupedGroup(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                insertUngroupedGroup(db)
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sources_list_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(DEFAULT_SERIOUS_GROUP_CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertUngroupedGroup(db: SupportSQLiteDatabase) {
            val now = System.currentTimeMillis()
            db.execSQL(
                """
                INSERT OR IGNORE INTO serious_groups
                (groupId, name, normalizedName, groupPriorityRank, isBuiltIn, createdAt, updatedAt)
                VALUES (${SeriousGroupEntity.UNGROUPED_GROUP_ID}, '${SeriousGroupEntity.UNGROUPED_GROUP_NAME}', 'ungrouped', NULL, 1, $now, $now)
                """.trimIndent()
            )
        }
    }
}
