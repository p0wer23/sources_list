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
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `sources` (`sourceId`,`url`,`title`,`bracket`,`isDone`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

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
        val _tmp_1: Int = if (entity.isDone) 1 else 0
        statement.bindLong(5, _tmp_1.toLong())
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfSourceEntity = object : EntityDeleteOrUpdateAdapter<SourceEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `sources` WHERE `sourceId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SourceEntity) {
        statement.bindLong(1, entity.sourceId)
      }
    }
    this.__updateAdapterOfSourceEntity = object : EntityDeleteOrUpdateAdapter<SourceEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `sources` SET `sourceId` = ?,`url` = ?,`title` = ?,`bracket` = ?,`isDone` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `sourceId` = ?"

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
        val _tmp_1: Int = if (entity.isDone) 1 else 0
        statement.bindLong(5, _tmp_1.toLong())
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        statement.bindLong(8, entity.sourceId)
      }
    }
  }

  public override suspend fun insert(source: SourceEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSourceEntity.insert(_connection, source)
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
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
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
          val _tmpIsDone: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsDone).toInt()
          _tmpIsDone = _tmp_2 != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = SourceEntity(_tmpSourceId,_tmpUrl,_tmpTitle,_tmpBracket,_tmpIsDone,_tmpCreatedAt,_tmpUpdatedAt)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
