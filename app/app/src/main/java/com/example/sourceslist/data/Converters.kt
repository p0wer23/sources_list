package com.example.sourceslist.data

import androidx.room.TypeConverter
import com.example.sourceslist.data.entity.BracketType

class Converters {
    @TypeConverter
    fun fromBracketType(value: BracketType): String = value.name

    @TypeConverter
    fun toBracketType(value: String): BracketType = BracketType.valueOf(value)
}
