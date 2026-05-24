package com.tdev.passmngr.data.db

import androidx.room.TypeConverter
import com.tdev.passmngr.data.model.Category

class Converters {
    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)
}
