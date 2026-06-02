package com.example.sourceslist.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.sourceslist.`data`.dao.SourceDao
import com.example.sourceslist.`data`.dao.SourceDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _sourceDao: Lazy<SourceDao> = lazy {
    SourceDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "9081bb0392edd664eb3b6f0037444a9f", "8807a2d1fa622e880d0909758ab914cb") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sources` (`sourceId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `url` TEXT NOT NULL, `title` TEXT, `bracket` TEXT NOT NULL, `priorityRank` INTEGER, `isDone` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_sources_bracket_priorityRank` ON `sources` (`bracket`, `priorityRank`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9081bb0392edd664eb3b6f0037444a9f')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `sources`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsSources: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSources.put("sourceId", TableInfo.Column("sourceId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("url", TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("title", TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("bracket", TableInfo.Column("bracket", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("priorityRank", TableInfo.Column("priorityRank", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("isDone", TableInfo.Column("isDone", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSources.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSources: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSources: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSources.add(TableInfo.Index("index_sources_bracket_priorityRank", true, listOf("bracket", "priorityRank"), listOf("ASC", "ASC")))
        val _infoSources: TableInfo = TableInfo("sources", _columnsSources, _foreignKeysSources, _indicesSources)
        val _existingSources: TableInfo = read(connection, "sources")
        if (!_infoSources.equals(_existingSources)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sources(com.example.sourceslist.data.entity.SourceEntity).
              | Expected:
              |""".trimMargin() + _infoSources + """
              |
              | Found:
              |""".trimMargin() + _existingSources)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "sources")
  }

  public override fun clearAllTables() {
    super.performClear(false, "sources")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(SourceDao::class, SourceDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun sourceDao(): SourceDao = _sourceDao.value
}
