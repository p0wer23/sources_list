package com.example.sourceslist.`data`.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.sourceslist.`data`.SeriousGroupSummary
import com.example.sourceslist.`data`.entity.SeriousGroupEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SeriousGroupDao_Impl(
  __db: RoomDatabase,
) : SeriousGroupDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSeriousGroupEntity: EntityInsertAdapter<SeriousGroupEntity>

  private val __deleteAdapterOfSeriousGroupEntity: EntityDeleteOrUpdateAdapter<SeriousGroupEntity>

  private val __updateAdapterOfSeriousGroupEntity: EntityDeleteOrUpdateAdapter<SeriousGroupEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSeriousGroupEntity = object : EntityInsertAdapter<SeriousGroupEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `serious_groups` (`groupId`,`name`,`normalizedName`,`groupPriorityRank`,`isBuiltIn`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SeriousGroupEntity) {
        statement.bindLong(1, entity.groupId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.normalizedName)
        val _tmpGroupPriorityRank: Int? = entity.groupPriorityRank
        if (_tmpGroupPriorityRank == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpGroupPriorityRank.toLong())
        }
        val _tmp: Int = if (entity.isBuiltIn) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfSeriousGroupEntity = object : EntityDeleteOrUpdateAdapter<SeriousGroupEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `serious_groups` WHERE `groupId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SeriousGroupEntity) {
        statement.bindLong(1, entity.groupId)
      }
    }
    this.__updateAdapterOfSeriousGroupEntity = object : EntityDeleteOrUpdateAdapter<SeriousGroupEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `serious_groups` SET `groupId` = ?,`name` = ?,`normalizedName` = ?,`groupPriorityRank` = ?,`isBuiltIn` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `groupId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SeriousGroupEntity) {
        statement.bindLong(1, entity.groupId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.normalizedName)
        val _tmpGroupPriorityRank: Int? = entity.groupPriorityRank
        if (_tmpGroupPriorityRank == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpGroupPriorityRank.toLong())
        }
        val _tmp: Int = if (entity.isBuiltIn) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        statement.bindLong(8, entity.groupId)
      }
    }
  }

  public override suspend fun insert(group: SeriousGroupEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSeriousGroupEntity.insertAndReturnId(_connection, group)
    _result
  }

  public override suspend fun delete(group: SeriousGroupEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfSeriousGroupEntity.handle(_connection, group)
  }

  public override suspend fun update(group: SeriousGroupEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfSeriousGroupEntity.handle(_connection, group)
  }

  public override fun observeSummaries(): Flow<List<SeriousGroupSummary>> {
    val _sql: String = """
        |
        |        SELECT
        |            sg.groupId AS groupId,
        |            sg.name AS name,
        |            sg.groupPriorityRank AS groupPriorityRank,
        |            sg.isBuiltIn AS isBuiltIn,
        |            sg.createdAt AS createdAt,
        |            sg.updatedAt AS updatedAt,
        |            COALESCE(SUM(CASE WHEN s.isDone = 0 THEN 1 ELSE 0 END), 0) AS activeCount,
        |            COALESCE(SUM(CASE WHEN s.isDone = 1 THEN 1 ELSE 0 END), 0) AS completedCount
        |        FROM serious_groups sg
        |        LEFT JOIN sources s
        |            ON COALESCE(s.seriousGroupId, 1) = sg.groupId
        |            AND s.bracket = 'SERIOUS'
        |        GROUP BY sg.groupId
        |        ORDER BY
        |            CASE WHEN sg.groupPriorityRank IS NULL THEN 1 ELSE 0 END ASC,
        |            sg.groupPriorityRank ASC,
        |            sg.createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("serious_groups", "sources")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfGroupId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfGroupPriorityRank: Int = 2
        val _columnIndexOfIsBuiltIn: Int = 3
        val _columnIndexOfCreatedAt: Int = 4
        val _columnIndexOfUpdatedAt: Int = 5
        val _columnIndexOfActiveCount: Int = 6
        val _columnIndexOfCompletedCount: Int = 7
        val _result: MutableList<SeriousGroupSummary> = mutableListOf()
        while (_stmt.step()) {
          val _item: SeriousGroupSummary
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpActiveCount: Int
          _tmpActiveCount = _stmt.getLong(_columnIndexOfActiveCount).toInt()
          val _tmpCompletedCount: Int
          _tmpCompletedCount = _stmt.getLong(_columnIndexOfCompletedCount).toInt()
          _item = SeriousGroupSummary(_tmpGroupId,_tmpName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt,_tmpActiveCount,_tmpCompletedCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeGroups(): Flow<List<SeriousGroupEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM serious_groups
        |        ORDER BY
        |            CASE WHEN groupPriorityRank IS NULL THEN 1 ELSE 0 END ASC,
        |            groupPriorityRank ASC,
        |            createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("serious_groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNormalizedName: Int = getColumnIndexOrThrow(_stmt, "normalizedName")
        val _columnIndexOfGroupPriorityRank: Int = getColumnIndexOrThrow(_stmt, "groupPriorityRank")
        val _columnIndexOfIsBuiltIn: Int = getColumnIndexOrThrow(_stmt, "isBuiltIn")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SeriousGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SeriousGroupEntity
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNormalizedName: String
          _tmpNormalizedName = _stmt.getText(_columnIndexOfNormalizedName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SeriousGroupEntity(_tmpGroupId,_tmpName,_tmpNormalizedName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeGroup(groupId: Long): Flow<SeriousGroupEntity?> {
    val _sql: String = "SELECT * FROM serious_groups WHERE groupId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("serious_groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, groupId)
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNormalizedName: Int = getColumnIndexOrThrow(_stmt, "normalizedName")
        val _columnIndexOfGroupPriorityRank: Int = getColumnIndexOrThrow(_stmt, "groupPriorityRank")
        val _columnIndexOfIsBuiltIn: Int = getColumnIndexOrThrow(_stmt, "isBuiltIn")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SeriousGroupEntity?
        if (_stmt.step()) {
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNormalizedName: String
          _tmpNormalizedName = _stmt.getText(_columnIndexOfNormalizedName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SeriousGroupEntity(_tmpGroupId,_tmpName,_tmpNormalizedName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getGroupById(groupId: Long): SeriousGroupEntity? {
    val _sql: String = "SELECT * FROM serious_groups WHERE groupId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, groupId)
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNormalizedName: Int = getColumnIndexOrThrow(_stmt, "normalizedName")
        val _columnIndexOfGroupPriorityRank: Int = getColumnIndexOrThrow(_stmt, "groupPriorityRank")
        val _columnIndexOfIsBuiltIn: Int = getColumnIndexOrThrow(_stmt, "isBuiltIn")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SeriousGroupEntity?
        if (_stmt.step()) {
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNormalizedName: String
          _tmpNormalizedName = _stmt.getText(_columnIndexOfNormalizedName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SeriousGroupEntity(_tmpGroupId,_tmpName,_tmpNormalizedName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getGroupByNormalizedName(normalizedName: String): SeriousGroupEntity? {
    val _sql: String = "SELECT * FROM serious_groups WHERE normalizedName = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, normalizedName)
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNormalizedName: Int = getColumnIndexOrThrow(_stmt, "normalizedName")
        val _columnIndexOfGroupPriorityRank: Int = getColumnIndexOrThrow(_stmt, "groupPriorityRank")
        val _columnIndexOfIsBuiltIn: Int = getColumnIndexOrThrow(_stmt, "isBuiltIn")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SeriousGroupEntity?
        if (_stmt.step()) {
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNormalizedName: String
          _tmpNormalizedName = _stmt.getText(_columnIndexOfNormalizedName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SeriousGroupEntity(_tmpGroupId,_tmpName,_tmpNormalizedName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getGroupByPriority(priorityRank: Int): SeriousGroupEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM serious_groups
        |        WHERE groupPriorityRank = ?
        |        LIMIT 1
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, priorityRank.toLong())
        val _columnIndexOfGroupId: Int = getColumnIndexOrThrow(_stmt, "groupId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfNormalizedName: Int = getColumnIndexOrThrow(_stmt, "normalizedName")
        val _columnIndexOfGroupPriorityRank: Int = getColumnIndexOrThrow(_stmt, "groupPriorityRank")
        val _columnIndexOfIsBuiltIn: Int = getColumnIndexOrThrow(_stmt, "isBuiltIn")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SeriousGroupEntity?
        if (_stmt.step()) {
          val _tmpGroupId: Long
          _tmpGroupId = _stmt.getLong(_columnIndexOfGroupId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpNormalizedName: String
          _tmpNormalizedName = _stmt.getText(_columnIndexOfNormalizedName)
          val _tmpGroupPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfGroupPriorityRank)) {
            _tmpGroupPriorityRank = null
          } else {
            _tmpGroupPriorityRank = _stmt.getLong(_columnIndexOfGroupPriorityRank).toInt()
          }
          val _tmpIsBuiltIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBuiltIn).toInt()
          _tmpIsBuiltIn = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SeriousGroupEntity(_tmpGroupId,_tmpName,_tmpNormalizedName,_tmpGroupPriorityRank,_tmpIsBuiltIn,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun compactPriorities(removedRank: Int, updatedAt: Long) {
    val _sql: String = """
        |
        |        UPDATE serious_groups
        |        SET groupPriorityRank = groupPriorityRank - 1, updatedAt = ?
        |        WHERE groupPriorityRank > ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, removedRank.toLong())
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
