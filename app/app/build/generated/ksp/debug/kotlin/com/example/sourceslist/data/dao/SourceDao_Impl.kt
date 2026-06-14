package com.example.sourceslist.`data`.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.sourceslist.`data`.Converters
import com.example.sourceslist.`data`.entity.BracketType
import com.example.sourceslist.`data`.entity.SourceEntity
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
public class SourceDao_Impl(
  __db: RoomDatabase,
) : SourceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSourceEntity: EntityInsertAdapter<SourceEntity>

  private val __converters: Converters = Converters()

  private val __deleteAdapterOfSourceEntity: EntityDeleteOrUpdateAdapter<SourceEntity>

  private val __updateAdapterOfSourceEntity: EntityDeleteOrUpdateAdapter<SourceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSourceEntity = object : EntityInsertAdapter<SourceEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `sources` (`sourceId`,`url`,`title`,`bracket`,`seriousGroupId`,`priorityRank`,`isDone`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SourceEntity) {
        statement.bindLong(1, entity.sourceId)
        statement.bindText(2, entity.url)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTitle)
        }
        val _tmp: String = __converters.fromBracketType(entity.bracket)
        statement.bindText(4, _tmp)
        val _tmpSeriousGroupId: Long? = entity.seriousGroupId
        if (_tmpSeriousGroupId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSeriousGroupId)
        }
        val _tmpPriorityRank: Int? = entity.priorityRank
        if (_tmpPriorityRank == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPriorityRank.toLong())
        }
        val _tmp_1: Int = if (entity.isDone) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfSourceEntity = object : EntityDeleteOrUpdateAdapter<SourceEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `sources` WHERE `sourceId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SourceEntity) {
        statement.bindLong(1, entity.sourceId)
      }
    }
    this.__updateAdapterOfSourceEntity = object : EntityDeleteOrUpdateAdapter<SourceEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `sources` SET `sourceId` = ?,`url` = ?,`title` = ?,`bracket` = ?,`seriousGroupId` = ?,`priorityRank` = ?,`isDone` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `sourceId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SourceEntity) {
        statement.bindLong(1, entity.sourceId)
        statement.bindText(2, entity.url)
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTitle)
        }
        val _tmp: String = __converters.fromBracketType(entity.bracket)
        statement.bindText(4, _tmp)
        val _tmpSeriousGroupId: Long? = entity.seriousGroupId
        if (_tmpSeriousGroupId == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSeriousGroupId)
        }
        val _tmpPriorityRank: Int? = entity.priorityRank
        if (_tmpPriorityRank == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpPriorityRank.toLong())
        }
        val _tmp_1: Int = if (entity.isDone) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        statement.bindLong(10, entity.sourceId)
      }
    }
  }

  public override suspend fun insert(source: SourceEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSourceEntity.insertAndReturnId(_connection, source)
    _result
  }

  public override suspend fun delete(source: SourceEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfSourceEntity.handle(_connection, source)
  }

  public override suspend fun update(source: SourceEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfSourceEntity.handle(_connection, source)
  }

  public override fun activeSourcesByBracket(bracket: BracketType): Flow<List<SourceEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ? AND isDone = 0
        |        ORDER BY
        |            CASE WHEN priorityRank IS NULL THEN 1 ELSE 0 END ASC,
        |            priorityRank ASC,
        |            createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sources")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SourceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SourceEntity
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun completedSourcesByBracket(bracket: BracketType): Flow<List<SourceEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ? AND isDone = 1
        |        ORDER BY createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sources")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SourceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SourceEntity
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun activeSourcesBySeriousGroup(bracket: BracketType, seriousGroupId: Long): Flow<List<SourceEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ? AND isDone = 0 AND COALESCE(seriousGroupId, 1) = ?
        |        ORDER BY
        |            CASE WHEN priorityRank IS NULL THEN 1 ELSE 0 END ASC,
        |            priorityRank ASC,
        |            createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sources")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, seriousGroupId)
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SourceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SourceEntity
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun completedSourcesBySeriousGroup(bracket: BracketType, seriousGroupId: Long): Flow<List<SourceEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ? AND isDone = 1 AND COALESCE(seriousGroupId, 1) = ?
        |        ORDER BY createdAt ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("sources")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, seriousGroupId)
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SourceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SourceEntity
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun duplicateCount(url: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM sources WHERE url = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, url)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSourceById(sourceId: Long): SourceEntity? {
    val _sql: String = "SELECT * FROM sources WHERE sourceId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sourceId)
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SourceEntity?
        if (_stmt.step()) {
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_1 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun prioritizedSourcesByScope(bracket: BracketType, seriousGroupId: Long?): List<SourceEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ?
        |            AND isDone = 0
        |            AND priorityRank IS NOT NULL
        |            AND (
        |                (? IS NULL AND seriousGroupId IS NULL)
        |                OR COALESCE(seriousGroupId, 1) = ?
        |            )
        |        ORDER BY priorityRank ASC
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 2
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        _argIndex = 3
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<SourceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SourceEntity
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSourceByScopeAndPriority(
    bracket: BracketType,
    seriousGroupId: Long?,
    priorityRank: Int,
  ): SourceEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM sources
        |        WHERE bracket = ?
        |            AND isDone = 0
        |            AND priorityRank = ?
        |            AND (
        |                (? IS NULL AND seriousGroupId IS NULL)
        |                OR COALESCE(seriousGroupId, 1) = ?
        |            )
        |        LIMIT 1
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, priorityRank.toLong())
        _argIndex = 3
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        _argIndex = 4
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        val _columnIndexOfSourceId: Int = getColumnIndexOrThrow(_stmt, "sourceId")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBracket: Int = getColumnIndexOrThrow(_stmt, "bracket")
        val _columnIndexOfSeriousGroupId: Int = getColumnIndexOrThrow(_stmt, "seriousGroupId")
        val _columnIndexOfPriorityRank: Int = getColumnIndexOrThrow(_stmt, "priorityRank")
        val _columnIndexOfIsDone: Int = getColumnIndexOrThrow(_stmt, "isDone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: SourceEntity?
        if (_stmt.step()) {
          val _tmpSourceId: Long
          _tmpSourceId = _stmt.getLong(_columnIndexOfSourceId)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpBracket: BracketType
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfBracket)
          _tmpBracket = __converters.toBracketType(_tmp_1)
          val _tmpSeriousGroupId: Long?
          if (_stmt.isNull(_columnIndexOfSeriousGroupId)) {
            _tmpSeriousGroupId = null
          } else {
            _tmpSeriousGroupId = _stmt.getLong(_columnIndexOfSeriousGroupId)
          }
          val _tmpPriorityRank: Int?
          if (_stmt.isNull(_columnIndexOfPriorityRank)) {
            _tmpPriorityRank = null
          } else {
            _tmpPriorityRank = _stmt.getLong(_columnIndexOfPriorityRank).toInt()
          }
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpSeriousGroupId,_tmpPriorityRank,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun compactPriorities(
    bracket: BracketType,
    seriousGroupId: Long?,
    removedRank: Int,
    updatedAt: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE sources
        |        SET priorityRank = priorityRank - 1, updatedAt = ?
        |        WHERE bracket = ?
        |            AND isDone = 0
        |            AND priorityRank > ?
        |            AND (
        |                (? IS NULL AND seriousGroupId IS NULL)
        |                OR COALESCE(seriousGroupId, 1) = ?
        |            )
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 2
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 3
        _stmt.bindLong(_argIndex, removedRank.toLong())
        _argIndex = 4
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        _argIndex = 5
        if (seriousGroupId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, seriousGroupId)
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun moveSeriousGroupSourcesToBracket(
    groupId: Long,
    bracket: BracketType,
    updatedAt: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE sources
        |        SET bracket = ?,
        |            seriousGroupId = NULL,
        |            priorityRank = NULL,
        |            isDone = 0,
        |            updatedAt = ?
        |        WHERE bracket = 'SERIOUS' AND seriousGroupId = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String = __converters.fromBracketType(bracket)
        _stmt.bindText(_argIndex, _tmp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindLong(_argIndex, groupId)
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
